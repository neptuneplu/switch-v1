package me.card.switchv1.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.PendingOutgoTrans;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

  private final ExecutorService businessExecutor;
  private final ApiClient apiClient;
  private final PersistentWorker persistentWorker;
  private final ApiCoder apiCoder;
  private final MessageCoder messageCoder;
  private final Class<Api> responseApiClz;
  private final DestinationURL destinationURL;
  private final PendingOutgoTrans pendingOutgoTrans = new PendingOutgoTrans();

  public DefaultProcessor(ApiClient apiClient,
                          PersistentWorker persistentWorker,
                          ApiCoder<? extends Api, ? extends Message> apiCoder,
                          MessageCoder messageCoder,
                          Class<Api> responseApiClz,
                          DestinationURL destinationURL) {

    this.businessExecutor = Executors.newFixedThreadPool(3,
        new ThreadFactoryBuilder()
            .setNameFormat("business-pool-%d")
            .setDaemon(true)
            .build());

    this.apiClient = apiClient;
    this.persistentWorker = persistentWorker;
    this.apiCoder = apiCoder;
    this.messageCoder = messageCoder;
    this.responseApiClz = responseApiClz;
    this.destinationURL = destinationURL;

  }


  @Override
  public void handleInboundAsync(RequestContext context) {
    businessExecutor.submit(() -> handleInbound0(context));
  }

  @Override
  public void handleOutboundResponseAsync(RequestContext context) {
    businessExecutor.submit(() -> handleOutbound0(context));
  }

  @Override
  public Future<Api> handleOutboundRequestAsync(RequestContext context) {
    Future<Api> future = pendingOutgoTrans.registerOutgo(context.getReponseApi().correlationId());
    businessExecutor.submit(() -> handleOutbound0(context));
    return future;
  }

  @Override
  public void handleOutboundAbnormalResponseAsync(RequestContext context) {
    businessExecutor.submit(() -> handleOutboundAbnormal0(context));
  }


  private void handleInbound0(RequestContext context) {
    context.markProcessRequestStart();

    logger.info("[stage2/5] processBusinessPre start: thread={}", Thread.currentThread().getName());

    try {
      Stream.of(context)
          .map(this::bytesToMsg)
          .map(this::msgToApi)
          .map(this::saveIncomeToDB)
          .forEach(this::route);
    } catch (Exception e) {
      logger.error("process request failed", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }

  }

  private RequestContext bytesToMsg(RequestContext requestContext) {
    Message msg = messageCoder.extract(requestContext.getIncomeBytes());
    requestContext.setIncomeMsg(msg);
    return requestContext;

  }

  private RequestContext msgToApi(RequestContext requestContext) {
    Api api = apiCoder.messageToApi(requestContext.getIncomeMsg());
    requestContext.setRequestApi(api);
    return requestContext;

  }

  private RequestContext saveIncomeToDB(RequestContext requestContext) {
    persistentWorker.saveInput(requestContext.getIncomeMsg());
    return requestContext;
  }

  private RequestContext route(RequestContext requestContext) {
    Api api = requestContext.getRequestApi();
    if ("0100".equals(api.mti())) {
      callApi(requestContext);
    } else {
      completePendingOutgo(requestContext);
    }
    return requestContext;
  }

  private void callApi(RequestContext requestContext) {
    requestContext.setResponseApiClz(responseApiClz);
    requestContext.setDestinationURL(destinationURL);

    try {
      apiClient.call(requestContext, this::handleOutboundResponseAsync,
          this::handleOutboundAbnormalResponseAsync);
    } catch (Exception e) {
      logger.error("ISS 业务预处理失败", e);
      requestContext.setError(e);
      sendSysFailureResponse(requestContext);
    }

  }

  private void completePendingOutgo(RequestContext requestContext) {
    pendingOutgoTrans.completeOutgo(requestContext.getRequestApi().correlationId(),
        requestContext.getRequestApi());
  }


  private void handleOutbound0(RequestContext context) {
    context.markProcessResponseStart();

    logger.info("[阶段4/5] 业务后处理开始: 线程={}", Thread.currentThread().getName());


    try {
      Stream.of(context)
          .map(this::apiToMsg)
          .map(this::msgToBytes)
          .map(this::saveOutgoToDB)
          .forEach(this::sendResponse);
    } catch (Exception e) {
      logger.error("process response failed", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }

  }

  private RequestContext apiToMsg(RequestContext requestContext) {
    Message msg = apiCoder.apiToMessage(requestContext.getReponseApi());
    requestContext.setOutgoMsg(msg);
    return requestContext;

  }

  private RequestContext msgToBytes(RequestContext requestContext) {
    ByteBuf byteMsg =
        Unpooled.unreleasableBuffer(messageCoder.compress(requestContext.getOutgoMsg()));
    requestContext.setOutgoBytes(byteMsg);
    return requestContext;
  }

  private RequestContext saveOutgoToDB(RequestContext requestContext) {
    persistentWorker.saveOutput(requestContext.getOutgoMsg());
    return requestContext;
  }


  private void sendResponse(RequestContext requestContext) {
    try {
      sendSuccessResponse(requestContext);
    } catch (Exception e) {
      logger.error("业务后处理失败", e);
      requestContext.setError(e);
      sendSysFailureResponse(requestContext);
    }
  }

  private void handleOutboundAbnormal0(RequestContext context) {
    sendSysFailureResponse(context);
  }

  private void sendSuccessResponse(RequestContext context) {
    //
    EventLoop nettyEventLoop = context.getChannel().eventLoop();

    logger.info("[阶段5/5] 提交写回任务: 当前线程={}, 目标线程={}",
        Thread.currentThread().getName(), nettyEventLoop);

    //
    nettyEventLoop.execute(() -> {
      logger.info("[阶段5/5] Netty写回: 线程={}", Thread.currentThread().getName());

      context.getChannel().writeAndFlush(context.getOutgoBytes())
          .addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
              context.logPerformance();
              logger.info("响应发送成功, 耗时: {}ms",
                  System.currentTimeMillis() - context.getStartTime());
            } else {
              context.setError(future.cause());
              logger.error("响应发送失败", future.cause());
            }
          });
    });
  }

  // TODO
  private void sendSysFailureResponse(RequestContext context) {
    EventLoop nettyEventLoop = context.getChannel().eventLoop();

    nettyEventLoop.execute(() -> {
      logger.info("[阶段 error] Netty write system failure msg: thread={}",
          Thread.currentThread().getName());

      // generate error byte message
      ByteBuf byteMsg;

      if (context.getReponseApi() != null) {
        Message msg = apiCoder.errorMessage(apiCoder.apiToMessage(context.getReponseApi()));
        byteMsg = messageCoder.compress(msg);
      } else {
        byteMsg = messageCoder.compress(apiCoder.errorMessage(context.getIncomeMsg()));

      }

      context.getChannel().writeAndFlush(byteMsg)
          .addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
              context.logPerformance();
              logger.info("system failure response send successful, time use: {}ms",
                  System.currentTimeMillis() - context.getStartTime());
            } else {
              context.setError(future.cause());
              logger.error("system failure response send failed", future.cause());
            }
          });
    });
  }

}
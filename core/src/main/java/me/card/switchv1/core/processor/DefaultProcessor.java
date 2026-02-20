package me.card.switchv1.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.BackofficeURL;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.MessageContext;
import me.card.switchv1.core.component.PendingOutgoTrans;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

  private final ExecutorService businessExecutor;
  private final PendingOutgoTrans pendingOutgoTrans;
  private ApiClient apiClient;
  private PersistentWorker persistentWorker;
  private ApiCoder apiCoder;
  private MessageCoder messageCoder;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;

  public DefaultProcessor() {

    this.businessExecutor = Executors.newFixedThreadPool(3,
        new ThreadFactoryBuilder()
            .setNameFormat("business-pool-%d")
            .setDaemon(true)
            .build());

    this.pendingOutgoTrans = new PendingOutgoTrans();

  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void setPersistentWorker(PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
  }

  public void setApiCoder(ApiCoder apiCoder) {
    this.apiCoder = apiCoder;
  }

  public void setMessageCoder(MessageCoder messageCoder) {
    this.messageCoder = messageCoder;
  }

  public void setResponseApiClz(Class<? extends Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
  }

  public void setBackofficeURL(BackofficeURL backofficeURL) {
    this.backofficeURL = backofficeURL;
  }

  @Override
  public void handleIncomeAsync(MessageContext context) {
    businessExecutor.submit(() -> handleIncome0(context));
  }

  @Override
  public void handleOutgoResponseAsync(MessageContext context) {
    businessExecutor.submit(() -> handleOutgo0(context));
  }

  @Override
  public void handleOutgoAbnormalResponseAsync(MessageContext context) {
    businessExecutor.submit(() -> handleOutgoAbnormal0(context));
  }


  @Override
  public CompletableFuture<Api> handleOutgoRequestAsync(MessageContext context) {
    CompletableFuture<Api> completableFuture =
        pendingOutgoTrans.registerOutgo(context.getOutgoApi().correlationId());
    businessExecutor.submit(() -> handleOutgo0(context));
    return completableFuture;
  }


  private void handleIncome0(MessageContext context) {
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

  private MessageContext bytesToMsg(MessageContext context) {
    Message msg = messageCoder.extract(context.getIncomeBytes());
    context.setIncomeMsg(msg);
    return context;

  }

  private MessageContext msgToApi(MessageContext context) {
    Api api = apiCoder.messageToApi(context.getIncomeMsg());
    context.setIncomeApi(api);
    return context;

  }

  private MessageContext saveIncomeToDB(MessageContext context) {
    persistentWorker.saveInput(context.getIncomeMsg());
    return context;
  }

  private void route(MessageContext context) {
    Api api = context.getIncomeApi();
    if ("0100".equals(api.mti())) {
      callApi(context);
    } else {
      completePendingOutgo(context);
    }
  }

  private void callApi(MessageContext context) {
    context.setResponseApiClz(responseApiClz);
    context.setDestinationURL(backofficeURL);

    try {
      apiClient.call(context, this::handleOutgoResponseAsync,
          this::handleOutgoAbnormalResponseAsync);
    } catch (Exception e) {
      logger.error("ISS 业务预处理失败", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }

  }

  private void completePendingOutgo(MessageContext context) {
    pendingOutgoTrans.completeOutgo(context.getIncomeApi().correlationId(),
        context.getIncomeApi());
  }


  private void handleOutgo0(MessageContext context) {
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

  private MessageContext apiToMsg(MessageContext context) {
    Message msg = apiCoder.apiToMessage(context.getOutgoApi());
    context.setOutgoMsg(msg);
    return context;

  }

  private MessageContext msgToBytes(MessageContext context) {
    ByteBuf byteMsg =
        Unpooled.unreleasableBuffer(messageCoder.compress(context.getOutgoMsg()));
    context.setOutgoBytes(byteMsg);
    return context;
  }

  private MessageContext saveOutgoToDB(MessageContext context) {
    persistentWorker.saveOutput(context.getOutgoMsg());
    return context;
  }


  private void sendResponse(MessageContext context) {
    try {
      sendSuccessResponse(context);
    } catch (Exception e) {
      logger.error("业务后处理失败", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }
  }

  private void handleOutgoAbnormal0(MessageContext context) {
    sendSysFailureResponse(context);
  }

  private void sendSuccessResponse(MessageContext context) {
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
  private void sendSysFailureResponse(MessageContext context) {
    EventLoop nettyEventLoop = context.getChannel().eventLoop();

    nettyEventLoop.execute(() -> {
      logger.info("[阶段 error] Netty write system failure msg: thread={}",
          Thread.currentThread().getName());

      // generate error byte message
      ByteBuf byteMsg;

      if (context.getOutgoApi() != null) {
        Message msg = apiCoder.errorMessage(apiCoder.apiToMessage(context.getOutgoApi()));
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
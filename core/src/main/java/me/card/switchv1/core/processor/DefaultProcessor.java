package me.card.switchv1.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.component.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

  private final ExecutorService businessExecutor;
  private final ApiCoder apiCoder;
  private final MessageCoder messageCoder;
  private final ApiClient apiClient;
  private final PersistentWorker persistentWorker;

  public DefaultProcessor(ApiCoder<? extends Api, ? extends Message> apiCoder,
                          MessageCoder messageCoder,
                          ApiClient apiClient,
                          PersistentWorker persistentWorker) {

    this.businessExecutor = Executors.newFixedThreadPool(3,
        new ThreadFactoryBuilder()
            .setNameFormat("business-pool-%d")
            .setDaemon(true)
            .build());

    this.apiCoder = apiCoder;
    this.messageCoder = messageCoder;
    this.apiClient = apiClient;
    this.persistentWorker = persistentWorker;
  }


  @Override
  public void processRequest(RequestContext context) {
    businessExecutor.submit(() -> processRequest0(context));
  }

  @Override
  public void processSuccessResponse(RequestContext context) {
    businessExecutor.submit(() -> processSuccessResponse0(context));
  }

  @Override
  public void processErrorResponse(RequestContext context) {
    businessExecutor.submit(() -> processErrorResponse0(context));
  }


  private void processRequest0(RequestContext context) {
    context.markProcessRequestStart();

    logger.info("[stage2/5] processBusinessPre start: thread={}", Thread.currentThread().getName());

    Stream.of(context)
        .map(this::bytesToMsg)
        .map(this::msgToApi)
        .map(this::saveIncomeToDB)
        .forEach(this::callApi);
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

  private void callApi(RequestContext requestContext) {
    try {
      apiClient.call(requestContext, this::processSuccessResponse, this::processErrorResponse);
    } catch (Exception e) {
      logger.error("业务预处理失败", e);
      sendErrorResponse(requestContext, "业务处理失败: " + e.getMessage());
    }
  }


  private void processSuccessResponse0(RequestContext context) {
    context.markProcessResponseStart();

    logger.info("[阶段4/5] 业务后处理开始: 线程={}", Thread.currentThread().getName());

    Stream.of(context)
        .map(this::apiToMsg)
        .map(this::msgToBytes)
        .map(this::saveOutgoToDB)
        .forEach(this::sendResponse);
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
      sendErrorResponse(requestContext, "后处理失败: " + e.getMessage());
    }
  }

  private void processErrorResponse0(RequestContext context) {
    sendErrorResponse(context, "error response");
  }

  private void sendSuccessResponse(RequestContext context) {
    //
    EventLoop nettyEventLoop = context.getCtx().channel().eventLoop();

    logger.info("[阶段5/5] 提交写回任务: 当前线程={}, 目标线程={}",
        Thread.currentThread().getName(), nettyEventLoop);

    //
    nettyEventLoop.execute(() -> {
      logger.info("[阶段5/5] Netty写回: 线程={}", Thread.currentThread().getName());

      context.getCtx().writeAndFlush(context.getOutgoBytes())
          .addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
              context.logPerformance();
              logger.info("响应发送成功, 耗时: {}ms",
                  System.currentTimeMillis() - context.getStartTime());
            } else {
              logger.error("响应发送失败", future.cause());
            }
          });
    });
  }

  private void sendErrorResponse(RequestContext context, String errorMessage) {
    logger.error("error response", errorMessage);
  }

}
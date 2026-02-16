package me.card.switchv1.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

  private final ExecutorService businessExecutor;
  private final ApiCoder<Api, Message> apiCoder;
  private final MessageCoder messageCoder;
  private final DestinationURL destinationURL;
  private final ApiClient apiClient;

  public DefaultProcessor(ApiCoder<Api, Message> apiCoder,
                          MessageCoder messageCoder,
                          DestinationURL destinationURL,
                          ApiClient apiClient) {

    this.apiCoder = apiCoder;
    this.messageCoder = messageCoder;
    this.destinationURL = destinationURL;
    this.apiClient = apiClient;

    this.businessExecutor = Executors.newFixedThreadPool(3,
        new ThreadFactoryBuilder()
            .setNameFormat("business-pool-%d")
            .setDaemon(true)
            .build());
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
    logger.info("[stage2/5] processBusinessPre start: thread={}", Thread.currentThread().getName());

    context.markBusinessPre();

    context.setDestinationURL(destinationURL);

    try {
      //
      Api api = apiCoder.messageToApi(messageCoder.extract(context.getIncomeMsg()));
      context.setRequestApi(api);

      //
      apiClient.call(context, this::processSuccessResponse, this::processErrorResponse);

    } catch (Exception e) {
      logger.error("业务预处理失败", e);
      sendErrorResponse(context, "业务处理失败: " + e.getMessage());
    }
  }


  private void processSuccessResponse0(RequestContext context) {
    context.markBusinessPost();
    logger.info("[阶段4/5] 业务后处理开始: 线程={}", Thread.currentThread().getName());

    Api api = context.getReponseApi();

    try {

      //
      Message message = apiCoder.apiToMessage(api);
      ByteBuf byteMsg = Unpooled.unreleasableBuffer(messageCoder.compress(message));

      //
      sendSuccessResponse(context, byteMsg);

    } catch (Exception e) {
      logger.error("业务后处理失败", e);
      sendErrorResponse(context, "后处理失败: " + e.getMessage());
    }
  }

  private void processErrorResponse0(RequestContext context) {
    sendErrorResponse(context, "error response");
  }

  private void sendSuccessResponse(RequestContext context, ByteBuf byteMsg) {
    //
    EventLoop nettyEventLoop = context.getCtx().channel().eventLoop();

    logger.info("[阶段5/5] 提交写回任务: 当前线程={}, 目标线程={}",
        Thread.currentThread().getName(), nettyEventLoop);

    //
    nettyEventLoop.execute(() -> {
      logger.info("[阶段5/5] Netty写回: 线程={}", Thread.currentThread().getName());

      context.getCtx()
          .writeAndFlush(byteMsg)
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
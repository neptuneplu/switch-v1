package me.card.switchv1.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
  private static final String NAME = "DefaultProcessor";

  private final ExecutorService businessExecutor;
  private final PendingOutgoTrans pendingOutgoTrans;
  private ApiClient apiClient;
  private PersistentWorker persistentWorker;
  private ApiCoder<Api, Message> apiCoder;
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

  public void setApiCoder(ApiCoder<Api, Message> apiCoder) {
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
  public void handleIncomeRequestAsync(MessageContext context) {
    businessExecutor.submit(() -> handleIncomeRequest(context));
  }

  @Override
  public void handleIncomeResponseAsync(MessageContext context) {
    businessExecutor.submit(() -> handleIncomeResponse(context));
  }

  @Override
  public CompletableFuture<Api> handleOutgoRequestAsync(MessageContext context) {
    CompletableFuture<Api> completableFuture =
        pendingOutgoTrans.registerOutgo(context.getOutgoApi().correlationId());

    businessExecutor.submit(() -> handleOutgo(context));

    return completableFuture;
  }

  @Override
  public void handleOutgoResponseAsync(MessageContext context) {
    businessExecutor.submit(() -> handleOutgo(context));
  }


  private void handleIncomeRequest(MessageContext context) {
    context.markProcessRequestStart();

    logger.debug("[stage {}] handleIncomeRequest start: thread={}", NAME,
        Thread.currentThread().getName());

    try {
      Stream.of(context)
          .map(this::msgToApi)
          .map(this::saveIncomeToDB)
          .forEach(this::doHandleIncomeRequest);
    } catch (Exception e) {
      logger.error("handleIncomeRequest failed", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }

  }

  private void handleIncomeResponse(MessageContext context) {
    context.markProcessRequestStart();

    logger.debug("[stage {}] handleIncomeResponse start: thread={}", NAME,
        Thread.currentThread().getName());

    try {
      Stream.of(context)
          .map(this::msgToApi)
          .map(this::saveIncomeToDB)
          .forEach(this::doHandleIncomeResponse);
    } catch (Exception e) {
      logger.error("handleIncomeResponse failed", e);
      context.setError(e);
      //todo, not to send response
      sendSysFailureResponse(context);
    }

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

  private void doHandleIncomeRequest(MessageContext context) {
    context.setResponseApiClz(responseApiClz);
    context.setDestinationURL(backofficeURL);

    apiClient.call(context)
        .thenAccept(api -> {
          context.setOutgoApi(api);
          handleOutgoResponseAsync(context);
        })
        .exceptionally(ex -> {
          logger.error("doHandleIncomeRequest failed", ex);
          context.setError(ex);
          sendSysFailureResponse(context);
          return null;
        });
  }

  private void doHandleIncomeResponse(MessageContext context) {
    pendingOutgoTrans.completeOutgo(context.getIncomeApi().correlationId(),
        context.getIncomeApi());
  }


  private void handleOutgo(MessageContext context) {
    context.markProcessResponseStart();

    logger.debug("[stage {}] handleOutgo start: thread={}", NAME, Thread.currentThread().getName());

    try {
      Stream.of(context)
          .map(this::apiToMsg)
          .map(this::saveOutgoToDB)
          .forEach(this::sendResponse);
    } catch (Exception e) {
      logger.error("handleOutgo failed", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }

  }

  private MessageContext apiToMsg(MessageContext context) {
    Message msg = apiCoder.apiToMessage(context.getOutgoApi());
    // to avoid save it to DB without seq no
    messageCoder.preCompressPcs(msg);
    context.setOutgoMsg(msg);
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
      logger.error("send response error", e);
      context.setError(e);
      sendSysFailureResponse(context);
    }
  }

  private void sendSuccessResponse(MessageContext context) {
    //
    EventLoop nettyEventLoop = context.getChannel().eventLoop();

    logger.info("[stage {}] sendSuccessResponse start: current thread={}, objective thread={}",
        NAME, Thread.currentThread().getName(), nettyEventLoop);

    //
    nettyEventLoop.execute(() -> {
      logger.info("[stage {}] Netty write: thread={}", NAME, Thread.currentThread().getName());

      context.getChannel().writeAndFlush(context.getOutgoMsg())
          .addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
              context.logPerformance();
              logger.info("send response successfully, time: {}ms",
                  System.currentTimeMillis() - context.getStartTime());
            } else {
              context.setError(future.cause());
              logger.error("send response failed", future.cause());
            }
          });
    });
  }

  // TODO
  private void sendSysFailureResponse(MessageContext context) {
    EventLoop nettyEventLoop = context.getChannel().eventLoop();

    nettyEventLoop.execute(() -> {
      logger.debug("[stage {}] sendSysFailureResponse start: thread={}", NAME,
          Thread.currentThread().getName());

      // generate error message
      Message msg;
      if (context.getOutgoApi() != null) {
        msg = apiCoder.errorMessage(apiCoder.apiToMessage(context.getOutgoApi()));
      } else {
        msg = apiCoder.errorMessage(context.getIncomeMsg());
      }

      context.getChannel().writeAndFlush(msg)
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
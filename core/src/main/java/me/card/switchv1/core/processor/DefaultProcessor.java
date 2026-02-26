package me.card.switchv1.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.component.BackofficeURL;
import me.card.switchv1.component.CorrelationId;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.component.MessageDirection;
import me.card.switchv1.component.PersistentWorker;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.connector.Connector;
import me.card.switchv1.core.internal.MessageContext;
import me.card.switchv1.core.internal.PendingIncomeTrans;
import me.card.switchv1.core.internal.PendingOutgoTrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);
  private static final String NAME = "DefaultProcessor";
  private static final int DEFAULT_ACQ_TRAN_TIMEOUT_SECONDS = 20;

  private final ExecutorService businessExecutor;
  private final PendingOutgoTrans pendingOutgoTrans;
  private final PendingIncomeTrans pendingIncomeTrans;
  private ApiClient apiClient;
  private PersistentWorker persistentWorker;
  private ApiCoder<Api, Message> apiCoder;
  private MessageCoder messageCoder;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
  private Connector connector;
  private int acqTranTimeoutSeconds;

  DefaultProcessor(int threadsNumber) {

    this.businessExecutor = Executors.newFixedThreadPool(threadsNumber,
        new ThreadFactoryBuilder()
            .setNameFormat("business-pool-%d")
            .setDaemon(true)
            .build());

    this.pendingOutgoTrans = new PendingOutgoTrans();
    this.pendingIncomeTrans = new PendingIncomeTrans();

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


  public void setConnector(Connector connector) {
    this.connector = connector;
  }

  public void setAcqTranTimeoutSeconds(int acqTranTimeoutSeconds) {
    this.acqTranTimeoutSeconds = acqTranTimeoutSeconds;
  }

  @Override
  public void handleIncomeRequestAsync(Message message) {
    MessageContext context = new MessageContext(MessageDirection.INCOME);
    context.markProcessStart();
    context.setIncomeMsg(message);
    pendingIncomeTrans.registerOutgo(message.correlationId(), context);

    businessExecutor.submit(() -> handleIncomeRequest(context));
  }

  @Override
  public void handleIncomeResponseAsync(Message message) {
    MessageContext context = pendingOutgoTrans.matchOutgo(message.correlationId());
    if (context == null) {
      logger.error("income response not found for correlationId {}", message.correlationId());
    }
    context.markRemoteEnd();
    context.setIncomeMsg(message);

    businessExecutor.submit(() -> handleIncomeResponse(context));
  }

  @Override
  public CompletableFuture<Api> handleOutgoRequestAsync(Api api) {

    if (connector == null) {
      logger.error("send outgo error, connector is not started");
      api.toResponse("96");
      return CompletableFuture.completedFuture(api);
    }

    MessageContext context = new MessageContext(MessageDirection.OUTGO);
    context.markProcessStart();

    CorrelationId correlationId = api.correlationId();

    CompletableFuture<Api> future = pendingOutgoTrans.registerOutgo(correlationId, context)
        .orTimeout(
            acqTranTimeoutSeconds == 0 ? DEFAULT_ACQ_TRAN_TIMEOUT_SECONDS : acqTranTimeoutSeconds,
            TimeUnit.SECONDS)
        .exceptionally(ex -> {
              if (ex instanceof TimeoutException) {
                api.toResponse("91");
                pendingOutgoTrans.completeOutgo(correlationId, api);
              } else {
                api.toResponse("96");
              }
              return api;
            }
        );


    context.setOutgoApi(api);

    businessExecutor.submit(() -> handleOutgoRequest(context));

    return future;
  }

  @Override
  public void handleOutgoResponseAsync(Api api) {
    MessageContext context = pendingIncomeTrans.matchOutgo(api.correlationId());
    if (context == null) {

    }
    context.markRemoteEnd();
    context.setOutgoApi(api);

    businessExecutor.submit(() -> handleOutgoResponse(context));
  }


  private void handleIncomeRequest(MessageContext context) {
    logger.debug("[stage {}] handleIncomeRequest start: thread={}", NAME,
        Thread.currentThread().getName());

    saveIncomeToDB(context);
    msgToApi(context);
    doHandleIncomeRequest(context);
  }

  private void handleIncomeResponse(MessageContext context) {
    logger.debug("[stage {}] handleIncomeResponse start: thread={}", NAME,
        Thread.currentThread().getName());

    saveIncomeToDB(context);
    msgToApi(context);
    doHandleIncomeResponse(context);

    context.markProcessEnd();
    context.logPerformance();
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
          handleOutgoResponseAsync(api);
        })
        .exceptionally(ex -> {
          logger.error("doHandleIncomeRequest failed", ex);
          context.setError(ex);
          sendErrorResponse(context);
          return null;
        });
  }

  private void doHandleIncomeResponse(MessageContext context) {
    pendingOutgoTrans.completeOutgo(context.getIncomeApi().correlationId(),
        context.getIncomeApi());
  }


  private void handleOutgoRequest(MessageContext context) {
    logger.debug("[stage {}] handleOutgoRequest start: thread={}", NAME,
        Thread.currentThread().getName());

    try {
      apiToMsg(context);
      saveOutgoToDB(context);
    } catch (Exception e) {
      logger.error("handleOutgoRequest failed", e);
      pendingOutgoErrorPcs(context.getOutgoMsg());
    }

    context.markRemoteStart();
    connector.write(context.getOutgoMsg(), null, this::pendingOutgoErrorPcs);
  }

  private void handleOutgoResponse(MessageContext context) {
    logger.debug("[stage {}] handleOutgoResponse start: thread={}", NAME,
        Thread.currentThread().getName());

    apiToMsg(context);
    saveOutgoToDB(context);


    connector.write(context.getOutgoMsg(),
        msg -> {
          context.markProcessEnd();
          context.logPerformance();
        },
        null);
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


  // todo
  private void sendErrorResponse(MessageContext context) {
    // generate error message
    Message msg;
    if (context.getOutgoApi() != null) {
      msg = apiCoder.errorMessage(apiCoder.apiToMessage(context.getOutgoApi()));
    } else {
      msg = apiCoder.errorMessage(context.getIncomeMsg());
    }

    connector.write(msg, null, null);
  }

  private void pendingOutgoErrorPcs(Message message) {
    Api api = apiCoder.messageToApi(message);
    api.toResponse("99");
    pendingOutgoTrans.completeOutgo(api.correlationId(), api);
  }

  @Override
  public int pendingOutgos() {
    return pendingOutgoTrans.pendingOutgoCount();
  }

}
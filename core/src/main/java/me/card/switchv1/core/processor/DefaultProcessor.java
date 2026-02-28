package me.card.switchv1.core.processor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.BackofficeURL;
import me.card.switchv1.component.CorrelationId;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageDirection;
import me.card.switchv1.component.PersistentWorker;
import me.card.switchv1.core.client.ApiClient;
import me.card.switchv1.core.connector.Connector;
import me.card.switchv1.core.internal.ApiContext;
import me.card.switchv1.core.internal.PendingIncomeTrans;
import me.card.switchv1.core.internal.PendingOutgoTrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);
  private static final String NAME = "DefaultProcessor";
  private static final int DEFAULT_ACQ_TRAN_TIMEOUT_SECONDS = 20;

  private final PendingOutgoTrans pendingOutgoTrans;
  private final PendingIncomeTrans pendingIncomeTrans;
  private ExecutorService executor;
  private ApiClient apiClient;
  private PersistentWorker persistentWorker;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
  private Connector connector;
  private int acqTranTimeoutSeconds;

  DefaultProcessor() {
    this.pendingOutgoTrans = new PendingOutgoTrans();
    this.pendingIncomeTrans = new PendingIncomeTrans();

  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void setPersistentWorker(PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
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

  public void setExecutor(ExecutorService executor) {
    this.executor = executor;
  }

  //
  //
  //

  @Override
  public void handleIncomeRequest(Api api) {
    ApiContext context = new ApiContext(MessageDirection.INCOME);
    context.setIncomeApi(api);
    context.markProcessStart();
    pendingIncomeTrans.registerOutgo(api.correlationId(), context);

    executor.submit(() -> doHandleIncomeRequest(context));
  }


  @Override
  public void handleOutgoResponse(Api api) {
    ApiContext context = pendingIncomeTrans.matchOutgo(api.correlationId());
    if (context == null) {

    }
    context.markRemoteEnd();
    context.setOutgoApi(api);

    executor.submit(() -> doHandleOutgoResponse(context));
  }


  @Override
  public CompletableFuture<Api> handleOutgoRequest(Api api) {

    if (connector == null) {
      logger.error("send outgo error, connector is not started");
      api.toResponse("96");
      return CompletableFuture.completedFuture(api);
    }

    ApiContext context = new ApiContext(MessageDirection.OUTGO);
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

    executor.submit(() -> doHandleOutgoRequest(context));

    return future;
  }

  @Override
  public void handleIncomeResponse(Api api) {
    ApiContext context = pendingOutgoTrans.matchOutgo(api.correlationId());
    if (context == null) {
      logger.error("income response not found for correlationId {}", api.correlationId());
    }
    context.setIncomeApi(api);
    context.markRemoteEnd();

    executor.submit(() -> doHandleIncomeResponse(context));
  }

  private void doHandleIncomeRequest(ApiContext context) {
    logger.debug("[stage {}] handleIncomeRequest start: thread={}", NAME,
        Thread.currentThread().getName());

    //todo check pendings

    //
    callApiClient(context);
  }

  private void doHandleOutgoResponse(ApiContext context) {
    logger.debug("[stage {}] handleOutgoResponse start: thread={}", NAME,
        Thread.currentThread().getName());

    connector.write(context.getOutgoApi(),
        msg -> {
          saveOutgoMessage(context.getOutgoApi().message());
          context.markProcessEnd();
          context.logPerformance();
        },
        null);
  }

  private void doHandleOutgoRequest(ApiContext context) {
    logger.debug("[stage {}] handleOutgoRequest start: thread={}", NAME,
        Thread.currentThread().getName());

    //

    //
    context.markRemoteStart();
    connector.write(context.getOutgoApi(),
        msg -> {
          saveOutgoMessage(context.getOutgoApi().message());
        },
        this::pendingOutgoErrorPcs);
  }

  private void doHandleIncomeResponse(ApiContext context) {
    logger.debug("[stage {}] handleIncomeResponse start: thread={}", NAME,
        Thread.currentThread().getName());

    saveIncomeMessage(context.getIncomeApi().message());

    pendingOutgoTrans.completeOutgo(context.getIncomeApi().correlationId(),
        context.getIncomeApi());

    context.markProcessEnd();
    context.logPerformance();
  }


  private void callApiClient(ApiContext context) {
    context.setResponseApiClz(responseApiClz);
    context.setDestinationURL(backofficeURL);

    apiClient.call(context)
        .thenAccept(api -> {
          saveIncomeMessage(context.getIncomeApi().message());
          handleOutgoResponse(api);
        })
        .exceptionally(ex -> {
          logger.error("doHandleIncomeRequest failed", ex);
          context.setError(ex);
          sendErrorResponse(context);
          return null;
        });
  }


  @Override
  public void saveIncomeMessage(Message message) {
    try {
      persistentWorker.saveIncomeMessage(message);

    } catch (Exception e) {
      logger.error("saveIncomeMessage failed", e);
    }
  }

  @Override
  public void saveOutgoMessage(Message message) {
    try {
      persistentWorker.saveOutgoMessage(message);

    } catch (Exception e) {
      logger.error("saveOutgoMessage failed", e);
    }
  }

  @Override
  public void saveIncomeError(Message message) {
    try {
      persistentWorker.saveIncomeError(message);

    } catch (Exception e) {
      logger.error("saveIncomeError failed", e);
    }
  }

  @Override
  public void saveOutgoError(Message message) {
    try {
      persistentWorker.saveOutgoError(message);

    } catch (Exception e) {
      logger.error("saveOutgoError failed", e);
    }
  }


  // todo
  private void sendErrorResponse(ApiContext context) {
    if (context.getOutgoApi() != null) {
      context.getOutgoApi().toResponse("96");
      connector.write(context.getOutgoApi(), null, null);
    } else {
      context.getIncomeApi().toResponse("96");
      connector.write(context.getIncomeApi(), null, null);

    }

  }

  private void pendingOutgoErrorPcs(Api api) {
    api.toResponse("99");
    pendingOutgoTrans.completeOutgo(api.correlationId(), api);
  }

  @Override
  public int pendingOutgos() {
    return pendingOutgoTrans.pendingOutgoCount();
  }

}
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
import me.card.switchv1.core.internal.PendingTrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessor implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);
  private static final String NAME = "DefaultProcessor";
  private static final int DEFAULT_ACQ_TRAN_TIMEOUT_SECONDS = 20;
  private static final int DEFAULT_ISS_TRAN_TIMEOUT_SECONDS = 15;

  private final PendingTrans pendingOutgoTrans;
  private final PendingTrans pendingIncomeTrans;
  private ExecutorService executor;
  private ApiClient apiClient;
  private PersistentWorker persistentWorker;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
  private Connector connector;
  private int acqTranTimeoutSeconds;
  private int issTranTimeoutSeconds;

  DefaultProcessor() {
    this.pendingOutgoTrans = new PendingTrans();
    this.pendingIncomeTrans = new PendingTrans();

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

  public void setIssTranTimeoutSeconds(int issTranTimeoutSeconds) {
    this.issTranTimeoutSeconds = issTranTimeoutSeconds;
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

    executor.submit(() -> doHandleIncomeRequest(context));
  }


  @Override
  public void handleOutgoResponse(Api api) {
    ApiContext context = pendingIncomeTrans.match(api.correlationId());
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

    CompletableFuture<Api> future = pendingOutgoTrans.register(correlationId, context)
        .orTimeout(
            acqTranTimeoutSeconds == 0 ? DEFAULT_ACQ_TRAN_TIMEOUT_SECONDS : acqTranTimeoutSeconds,
            TimeUnit.SECONDS)
        .exceptionally(ex -> {
              if (ex instanceof TimeoutException) {
                api.toResponse("91");
                pendingOutgoTrans.complete(correlationId, api);
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
    ApiContext context = pendingOutgoTrans.match(api.correlationId());
    if (context == null) {
      logger.error("income response not found for correlationId {}", api.correlationId());
    }
    context.setIncomeApi(api);
    context.markRemoteEnd();

    executor.submit(() -> doHandleIncomeResponse(context));
  }

  private void doHandleIncomeRequest(ApiContext context) {
    logger.debug("doHandleIncomeRequest start");

    Api api = context.getIncomeApi();

    saveIncomeMessage(api.message());

    pendingIncomeTrans.register(api.correlationId(), context)
        .orTimeout(
            issTranTimeoutSeconds == 0 ? DEFAULT_ISS_TRAN_TIMEOUT_SECONDS : issTranTimeoutSeconds,
            TimeUnit.SECONDS)
        .exceptionally(ex -> {
          if (ex instanceof TimeoutException) {
            pendingIncomeTrans.complete(api.correlationId(), api);
          } else {
            logger.error(ex.getMessage(), ex);
          }
          return api;
        })
    ;
    //
    callApiClient(context);
  }

  private void doHandleOutgoResponse(ApiContext context) {
    logger.debug("handleOutgoResponse start");

    connector.write(context.getOutgoApi(),
        msg -> {
          saveOutgoMessage(context.getOutgoApi().message());
          pendingIncomeTrans.complete(context.getOutgoApi().correlationId(), context.getOutgoApi());
          context.markProcessEnd();
          context.logPerformance();
        },
        null);

  }

  private void doHandleOutgoRequest(ApiContext context) {
    logger.debug("doHandleOutgoRequest start");


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
    logger.debug("doHandleIncomeResponse start");

    saveIncomeMessage(context.getIncomeApi().message());

    pendingOutgoTrans.complete(context.getIncomeApi().correlationId(),
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
    pendingOutgoTrans.complete(api.correlationId(), api);
  }

  @Override
  public int pendingOutgos() {
    return pendingOutgoTrans.pendingCount();
  }

  @Override
  public int pendingIncomes() {
    return pendingIncomeTrans.pendingCount();
  }

}
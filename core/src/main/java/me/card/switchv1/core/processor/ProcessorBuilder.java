package me.card.switchv1.core.processor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.component.BackofficeURL;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.component.PersistentWorker;
import me.card.switchv1.core.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorBuilder {
  private static final Logger logger = LoggerFactory.getLogger(ProcessorBuilder.class);

  private ApiClient apiClient;
  private PersistentWorker persistentWorker;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
  private int acqTranTimeoutSeconds;
  private int issTranTimeoutSeconds;
  private ExecutorService executor;

  public ProcessorBuilder apiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
    return this;
  }

  public ProcessorBuilder persistentWorker(
      PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
    return this;
  }

  public ProcessorBuilder responseApiClz(
      Class<? extends Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
    return this;
  }

  public ProcessorBuilder backofficeURL(
      BackofficeURL backofficeURL) {
    this.backofficeURL = backofficeURL;
    return this;
  }

  public ProcessorBuilder acqTranTimeoutSeconds(int acqTranTimeoutSeconds) {
    this.acqTranTimeoutSeconds = acqTranTimeoutSeconds;
    return this;
  }

  public ProcessorBuilder issTranTimeoutSeconds(int issTranTimeoutSeconds) {
    this.issTranTimeoutSeconds = issTranTimeoutSeconds;
    return this;
  }

  public ProcessorBuilder executor(ExecutorService executor) {
    this.executor = executor;
    return this;

  }

  public Processor build() {
    logger.debug("processor build start");
    DefaultProcessor processor = new DefaultProcessor();
    processor.setApiClient(apiClient);
    processor.setResponseApiClz(responseApiClz);
    processor.setPersistentWorker(persistentWorker);
    processor.setBackofficeURL(backofficeURL);
    processor.setAcqTranTimeoutSeconds(acqTranTimeoutSeconds);
    processor.setIssTranTimeoutSeconds(issTranTimeoutSeconds);
    processor.setExecutor(executor);

    return processor;
  }


}

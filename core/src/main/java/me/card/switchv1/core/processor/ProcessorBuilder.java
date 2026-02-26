package me.card.switchv1.core.processor;

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
  private ApiCoder<Api, Message> apiCoder;
  private MessageCoder messageCoder;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
  private int threadsNumber;
  private int acqTranTimeoutSeconds;

  public ProcessorBuilder apiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
    return this;
  }

  public ProcessorBuilder persistentWorker(
      PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
    return this;
  }

  public ProcessorBuilder apiCoder(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
    return this;
  }

  public ProcessorBuilder messageCoder(MessageCoder messageCoder) {
    this.messageCoder = messageCoder;
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

  public ProcessorBuilder setThreadsNumber(int threadsNumber) {
    this.threadsNumber = threadsNumber;
    return this;
  }

  public ProcessorBuilder setAcqTranTimeoutSeconds(int acqTranTimeoutSeconds) {
    this.acqTranTimeoutSeconds = acqTranTimeoutSeconds;
    return this;
  }

  public Processor build() {
    logger.debug("processor build start");
    DefaultProcessor processor = new DefaultProcessor(threadsNumber);
    processor.setApiClient(apiClient);
    processor.setApiCoder(apiCoder);
    processor.setMessageCoder(messageCoder);
    processor.setResponseApiClz(responseApiClz);
    processor.setPersistentWorker(persistentWorker);
    processor.setBackofficeURL(backofficeURL);
    processor.setAcqTranTimeoutSeconds(acqTranTimeoutSeconds);

    return processor;
  }


}

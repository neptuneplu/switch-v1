package me.card.switchv1.core.internal;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.BackofficeURL;
import me.card.switchv1.component.MessageDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiContext {
  private static final Logger logger = LoggerFactory.getLogger(ApiContext.class);

  private MessageDirection messageDirection;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
//  private Message incomeMsg;
//  private Message outgoMsg;
  private Api incomeApi;
  private Api outgoApi;
  private final Map<String, Object> businessData;
  private Throwable error;
  private CompletableFuture<Api> resultFuture;

  private final long startTime;
  long processStartTime;
  long remoteStartTime;
  long remoteEndTime;
  long processEndTime;

  public ApiContext(MessageDirection messageDirection) {
    this.messageDirection = messageDirection;
    this.startTime = System.currentTimeMillis();
    this.businessData = new ConcurrentHashMap<>();
  }

  public MessageDirection getMessageDirection() {
    return messageDirection;
  }

  public ApiContext setMessageDirection(
      MessageDirection messageDirection) {
    this.messageDirection = messageDirection;
    return this;
  }

  public Class<? extends Api> getResponseApiClz() {
    return responseApiClz;
  }

  public void setResponseApiClz(Class<? extends Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public BackofficeURL getDestinationURL() {
    return backofficeURL;
  }

  public void setDestinationURL(BackofficeURL backofficeURL) {
    this.backofficeURL = backofficeURL;
  }

//  public Message getIncomeMsg() {
//    return incomeMsg;
//  }
//
//  public void setIncomeMsg(Message incomeMsg) {
//    this.incomeMsg = incomeMsg;
//  }
//
//  public Message getOutgoMsg() {
//    return outgoMsg;
//  }
//
//  public void setOutgoMsg(Message outgoMsg) {
//    this.outgoMsg = outgoMsg;
//  }

  public Api getIncomeApi() {
    return incomeApi;
  }

  public void setIncomeApi(Api incomeApi) {
    this.incomeApi = incomeApi;
  }

  public Api getOutgoApi() {
    return outgoApi;
  }

  public void setOutgoApi(Api outgoApi) {
    this.outgoApi = outgoApi;
  }

  public Map<String, Object> getBusinessData() {
    return businessData;
  }

  public CompletableFuture<Api> getResultFuture() {
    return resultFuture;
  }

  public void setResultFuture(CompletableFuture<Api> resultFuture) {
    this.resultFuture = resultFuture;
  }

  public long getStartTime() {
    return startTime;
  }

  public void markProcessStart() {
    processStartTime = System.currentTimeMillis();
  }

  public void markRemoteStart() {
    remoteStartTime = System.currentTimeMillis();
  }

  public void markRemoteEnd() {
    remoteEndTime = System.currentTimeMillis();
  }

  public void markProcessEnd() {
    processEndTime = System.currentTimeMillis();
  }

  public void logPerformance() {
    logger.info(
        "performance metrics - total time consumed: {}ms, req: {}ms, remote : {}ms, resp: {}ms",
        (processEndTime - processStartTime),
        (remoteStartTime - processStartTime),
        (remoteEndTime - remoteStartTime),
        (processEndTime - remoteEndTime));
  }
}

package me.card.switchv1.core.component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageContext {
  private static final Logger logger = LoggerFactory.getLogger(MessageContext.class);

  private Channel channel;
  private Class<? extends Api> responseApiClz;
  private BackofficeURL backofficeURL;
  private Message incomeMsg;
  private Message outgoMsg;
  private Api incomeApi;
  private Api outgoApi;
  private final Map<String, Object> businessData;
  private Throwable error;

  private final long startTime;
  long nettyReceiveTime;
  long processRequestStartTime;
  long httpStartTime;
  long httpEndTime;
  long processResponseStartTime;

  public MessageContext(Channel channel) {
    this.channel = channel;
    this.startTime = System.currentTimeMillis();
    this.nettyReceiveTime = startTime;
    this.businessData = new ConcurrentHashMap<>();
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


  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public Message getIncomeMsg() {
    return incomeMsg;
  }

  public void setIncomeMsg(Message incomeMsg) {
    this.incomeMsg = incomeMsg;
  }

  public Message getOutgoMsg() {
    return outgoMsg;
  }

  public void setOutgoMsg(Message outgoMsg) {
    this.outgoMsg = outgoMsg;
  }

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

  public long getStartTime() {
    return startTime;
  }

  public void markProcessRequestStart() {
    processRequestStartTime = System.currentTimeMillis();
  }

  public void markHttpStart() {
    httpStartTime = System.currentTimeMillis();
  }

  public void markHttpEnd() {
    httpEndTime = System.currentTimeMillis();
  }

  public void markProcessResponseStart() {
    processResponseStartTime = System.currentTimeMillis();
  }

  public void logPerformance() {
    logger.info("性能统计 - 总耗时: {}ms, 业务预处理: {}ms, HTTP调用: {}ms, 业务后处理: {}ms",
        (processResponseStartTime - nettyReceiveTime),
        (httpStartTime - processRequestStartTime),
        (httpEndTime - httpStartTime),
        (processResponseStartTime - httpEndTime));
  }
}

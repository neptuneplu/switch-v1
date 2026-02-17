package me.card.switchv1.core.component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContext {
  private static final Logger logger = LoggerFactory.getLogger(RequestContext.class);

  private ChannelHandlerContext ctx;
  private Class<Api> responseApiClz;
  private DestinationURL destinationURL;
  private ByteBuf incomeBytes;
  private ByteBuf outgoBytes;
  private Message incomeMsg;
  private Message outgoMsg;
  private Api requestApi;
  private Api reponseApi;
  private final Map<String, Object> businessData;
  private Throwable error;

  private final long startTime;
  long nettyReceiveTime;
  long processRequestStartTime;
  long httpStartTime;
  long httpEndTime;
  long processResponseStartTime;

  public RequestContext(ChannelHandlerContext ctx, ByteBuf incomeBytes) {
    this.ctx = ctx;
    this.incomeBytes = incomeBytes;
    this.startTime = System.currentTimeMillis();
    this.nettyReceiveTime = startTime;
    this.businessData = new ConcurrentHashMap<>();
  }

  public Class<Api> getResponseApiClz() {
    return responseApiClz;
  }

  public void setResponseApiClz(Class<Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public DestinationURL getDestinationURL() {
    return destinationURL;
  }

  public void setDestinationURL(DestinationURL destinationURL) {
    this.destinationURL = destinationURL;
  }


  public ChannelHandlerContext getCtx() {
    return ctx;
  }

  public void setCtx(ChannelHandlerContext ctx) {
    this.ctx = ctx;
  }

  public ByteBuf getIncomeBytes() {
    return incomeBytes;
  }

  public void setIncomeBytes(ByteBuf incomeBytes) {
    this.incomeBytes = incomeBytes;
  }

  public ByteBuf getOutgoBytes() {
    return outgoBytes;
  }

  public void setOutgoBytes(ByteBuf outgoBytes) {
    this.outgoBytes = outgoBytes;
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

  public Api getRequestApi() {
    return requestApi;
  }

  public void setRequestApi(Api requestApi) {
    this.requestApi = requestApi;
  }

  public Api getReponseApi() {
    return reponseApi;
  }

  public void setReponseApi(Api reponseApi) {
    this.reponseApi = reponseApi;
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

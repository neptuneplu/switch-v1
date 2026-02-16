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
  private DestinationURL destinationURL;
  private ByteBuf originalRequest;
  private final Map<String, Object> businessData;
  private Throwable error;

  private final long startTime;
  long nettyReceiveTime;
  long businessPreTime;
  long httpStartTime;
  long httpEndTime;
  long businessPostTime;

  public RequestContext(ChannelHandlerContext ctx, ByteBuf request) {
    this.ctx = ctx;
    this.originalRequest = request;
    this.startTime = System.currentTimeMillis();
    this.nettyReceiveTime = startTime;
    this.businessData = new ConcurrentHashMap<>();
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

  public ByteBuf getOriginalRequest() {
    return originalRequest;
  }

  public void setOriginalRequest(ByteBuf originalRequest) {
    this.originalRequest = originalRequest;
  }

  public Map<String, Object> getBusinessData() {
    return businessData;
  }

  public long getStartTime() {
    return startTime;
  }

  // 记录各阶段时间
  public void markBusinessPre() {
    businessPreTime = System.currentTimeMillis();
  }

  public void markHttpStart() {
    httpStartTime = System.currentTimeMillis();
  }

  public void markHttpEnd() {
    httpEndTime = System.currentTimeMillis();
  }

  public void markBusinessPost() {
    businessPostTime = System.currentTimeMillis();
  }

  // 打印性能日志
  public void logPerformance() {
    logger.info("性能统计 - 总耗时: {}ms, 业务预处理: {}ms, HTTP调用: {}ms, 业务后处理: {}ms",
        (businessPostTime - nettyReceiveTime),
        (httpStartTime - businessPreTime),
        (httpEndTime - httpStartTime),
        (businessPostTime - httpEndTime));
  }
}

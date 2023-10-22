package me.card.switchv1.core.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.util.List;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHttpRequestHandler extends MessageToMessageEncoder<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHttpRequestHandler.class);

  private final DestinationURL destinationURL;

  public BackOfficeHttpRequestHandler(DestinationURL destinationURL) {
    this.destinationURL = destinationURL;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Api api, List<Object> out) {
    logger.debug("BackOfficeHttpRequestHandler start");

    ObjectMapper mapper = new ObjectMapper();
    String strApi;
    try {
      strApi = mapper.writeValueAsString(api);
    } catch (JsonProcessingException e) {
      logger.error("jackson parser write error", e);
      throw new HandlerException("jackson parser write error");
    }

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("strApi: %s", strApi));
    }

    ByteBuf bufRequest = Unpooled.copiedBuffer(strApi, CharsetUtil.UTF_8);

    FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
        HttpVersion.HTTP_1_1,
        HttpMethod.POST,
        destinationURL.getUri().toString(),
        bufRequest
    );


    fullHttpRequest.headers()
        .set(HttpHeaderNames.HOST, destinationURL.getHostStr());
    fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH,
        fullHttpRequest.content().readableBytes());
    fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("fullHttpRequest: %s", fullHttpRequest));
    }

    out.add(fullHttpRequest);
  }
}

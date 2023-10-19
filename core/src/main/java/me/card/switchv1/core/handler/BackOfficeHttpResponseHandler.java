package me.card.switchv1.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.FullHttpResponse;
import java.util.List;
import me.card.switchv1.core.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHttpResponseHandler extends MessageToMessageDecoder<FullHttpResponse> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHttpResponseHandler.class);

  private final Class<? extends Api> responseApiClz;

  public BackOfficeHttpResponseHandler(Class<? extends Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, FullHttpResponse response, List<Object> out)
      throws Exception {
    logger.debug("BackOfficeHttpResponseHandler start");

    byte[] byteContent = new byte[response.content().readableBytes()];
    response.content().readBytes(byteContent);

    ObjectMapper mapper = new ObjectMapper();
    Api api;
    try {
      api = mapper.readValue(byteContent, responseApiClz);
    } catch (Exception e) {
      logger.error("jackson parser read error", e);
      throw new HandlerException("jackson parser read error");
    }

    out.add(api);

  }
}

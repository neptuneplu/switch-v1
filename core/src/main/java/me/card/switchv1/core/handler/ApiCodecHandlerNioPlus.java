package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiCodecHandlerNioPlus extends MessageToMessageCodec<Api, Message> {
  private static final Logger logger = LoggerFactory.getLogger(ApiCodecHandlerNioPlus.class);

  private final ApiCoder<Api, Message> apiCoder;

  public ApiCodecHandlerNioPlus(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
    logger.debug("api decode start");

    try {
      Api api = apiCoder.messageToApi(msg);
      out.add(api);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("api decode failed!!!", e);
      logger.warn("**************************");
      ctx.writeAndFlush(apiCoder.errorMessage(msg));
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Api api, List<Object> out) throws Exception {
    logger.debug("api encode start");

    try {
      Message message = apiCoder.apiToMessage(api);
      out.add(message);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("api encode failed!!!", e);
      logger.warn("**************************");
    }
  }
}

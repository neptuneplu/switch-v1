package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ApiCodecHandler extends MessageToMessageCodec<Message, Api> {
  private static final Logger logger = LoggerFactory.getLogger(ApiCodecHandler.class);
  public static final String NAME = "ApiCodecHandler";

  private final ApiCoder<Api, Message> apiCoder;

  public ApiCodecHandler(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Api api, List<Object> out) {
    logger.debug("api encode start");

    try {
      Message message = apiCoder.apiToMessage(api);
      out.add(message);
    } catch (Exception e) {
      logger.warn("api encode failed!!!", e);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("api decode start");

    try {
      Api api = apiCoder.messageToApi(msg);
      out.add(api);
    } catch (Exception e) {
      logger.warn("api decode failed!!!", e);
      ctx.writeAndFlush(apiCoder.errorMessage(msg));
    }
  }
}

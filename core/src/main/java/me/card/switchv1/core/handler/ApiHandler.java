package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.component.Message;
import me.card.switchv1.core.handler.attributes.ChannelAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ApiHandler extends MessageToMessageCodec<Message, Api> {
  private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);
  public static final String NAME = "ApiHandler";

  private final ApiCoder<Api, Message> apiCoder;

  public ApiHandler(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Api api, List<Object> out) {
    logger.debug("[stage {}] encode start: thread={}", NAME,
        Thread.currentThread().getName());
//    ctx.channel().attr(ChannelAttributes.MESSAGE).set(api);

    try {
      Message message = apiCoder.apiToMessage(api);
      out.add(message);
    } catch (Exception e) {
      throw new ApiHandlerException("api encode failed, " + e.getMessage());
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("[stage {}] decode start: thread={}", NAME,
        Thread.currentThread().getName());

    ctx.channel().attr(ChannelAttributes.MESSAGE).set(msg);

    try {
      Api api = apiCoder.messageToApi(msg);
      out.add(api);
    } catch (Exception e) {
      throw new ApiHandlerException("api decode failed, " + e.getMessage());
    }
  }
}

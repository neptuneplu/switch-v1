package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiDecodeHandler extends MessageToMessageDecoder<Message> {
  private static final Logger logger = LoggerFactory.getLogger(ApiDecodeHandler.class);

  private final ApiCoder<Api, Message> apiCoder;

  public ApiDecodeHandler(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
  }

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, Message message,
                        List<Object> list) {
    logger.debug("api decode start");

    try {
      Api api = apiCoder.messageToApi(message);
      list.add(api);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("api decode failed!!!", e);
      logger.warn("**************************");
      channelHandlerContext.writeAndFlush(apiCoder.errorMessage(message));
    }
  }


}

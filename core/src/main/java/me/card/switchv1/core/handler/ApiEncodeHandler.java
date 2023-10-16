package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiEncodeHandler extends MessageToMessageEncoder<Api> {
  private static final Logger logger = LoggerFactory.getLogger(ApiEncodeHandler.class);

  private final ApiCoder<Api, Message> apiCoder;

  public ApiEncodeHandler(ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
  }

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Api api,
                        List<Object> list) {
    logger.debug("api encode start");

    try {
      Message message = apiCoder.apiToMessage(api);
      list.add(message);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("api encode failed!!!", e);
      logger.warn("**************************");
    }

  }
}

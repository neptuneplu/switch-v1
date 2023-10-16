package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import me.card.switchv1.core.component.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCompressHandler extends MessageToMessageEncoder<Message> {
  private static final Logger logger = LoggerFactory.getLogger(MessageCompressHandler.class);


  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext,
                        Message message, List<Object> list) {
    logger.debug("compress mdg start");

    try {
      byte[] byteMsg = message.compress();
      list.add(byteMsg);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("compress message failed!!!", e);
      logger.warn("**************************");
      //todo
      logger.error(String.format("extract message failed, msg: %s", message.toString()));
    }

  }

}

package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Message;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageExtractHandler extends MessageToMessageDecoder<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(MessageExtractHandler.class);

  private final Supplier<Message> messageSupplier;

  public MessageExtractHandler(Supplier<Message> messageSupplier) {
    this.messageSupplier = messageSupplier;
  }

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, byte[] bytes,
                        List<Object> list) {
    logger.debug("extract mdg start");

    try {
      Message message = messageSupplier.get();
      message.extract(bytes);
      message.print();
      list.add(message);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("extract message failed!!!", e);
      logger.warn("**************************");
      logger.error(String.format("extract message failed, msg: %s", ISOUtil.byte2hex(bytes)));
    }
  }


}

package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Message;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandlerNioPlus extends MessageToMessageCodec<Message, byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(MessageHandlerNioPlus.class);

  private final Supplier<Message> messageSupplier;

  public MessageHandlerNioPlus(Supplier<Message> messageSupplier) {
    this.messageSupplier = messageSupplier;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
    logger.debug("extract mdg start");

    try {
      Message message = messageSupplier.get();
      message.extract(msg);
      message.print();
      out.add(message);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("extract message failed!!!", e);
      logger.warn("**************************");
      logger.error(String.format("extract message failed, msg: %s", ISOUtil.byte2hex(msg)));
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
    logger.debug("compress mdg start");

    try {
      byte[] byteMsg = msg.compress();
      out.add(byteMsg);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("compress message failed!!!", e);
      logger.warn("**************************");
      //todo
      logger.error(String.format("extract message failed, msg: %s", msg.toString()));
    }
  }

}

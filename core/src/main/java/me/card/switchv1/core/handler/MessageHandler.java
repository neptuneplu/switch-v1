package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.core.handler.attributes.ChannelAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class MessageHandler extends MessageToMessageCodec<ByteBuf, Message> {
  private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
  public static final String NAME = "MessageHandler";

  private final MessageCoder messageCoder;

  public MessageHandler(MessageCoder messageCoder) {
    this.messageCoder = messageCoder;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("encode start");

    try {
      //todo test
//      out.add(Unpooled.unreleasableBuffer(messageCoder.compress(msg)));
      throw new RuntimeException("test exception");
    } catch (Exception e) {
      logger.error("compress message failed, msg: {}", msg);
      throw new MessageHandlerException("message compress failed, " + e.getMessage());

    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf bytes, List<Object> out) {
    logger.debug("decode start");

    try {
      out.add(messageCoder.extract(bytes));
    } catch (Exception e) {
      logger.error("extract message failed, bytes: {}", ByteBufUtil.hexDump(bytes));
      throw new MessageHandlerException("message extract failed, " + e.getMessage());
    }
  }

}

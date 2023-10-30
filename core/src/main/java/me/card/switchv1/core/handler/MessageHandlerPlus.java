package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class MessageHandlerPlus extends MessageToMessageCodec<Message, byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(MessageHandlerPlus.class);
  public static final String NAME = "MessageHandlerNioPlus";

  private final MessageCoder messageCoder;

  public MessageHandlerPlus(MessageCoder messageCoder) {
    this.messageCoder = messageCoder;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) {
    logger.debug("extract mdg start");

    try {
      Message message = messageCoder.extract(msg);
      out.add(message);
    } catch (Exception e) {
      logger.warn("extract message failed!!!", e);
      logger.error(String.format("extract message failed, msg: %s", ISOUtil.byte2hex(msg)));
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("compress mdg start");

    try {
      byte[] byteMsg = messageCoder.compress(msg);
      out.add(byteMsg);
    } catch (Exception e) {
      logger.warn("compress message failed!!!", e);
      logger.error(String.format("extract message failed, msg: %s", msg.toString()));
    }
  }

}

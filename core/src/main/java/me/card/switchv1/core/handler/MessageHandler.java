package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends MessageToMessageCodec<byte[], Message> {
  private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);


  private final Supplier<Message> messageSupplier;
  private final Id id;

  public MessageHandler(Supplier<Message> messageSupplier, Id id) {
    this.messageSupplier = messageSupplier;
    this.id = id;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("compress msg start, ctx: " + ctx + " ctx executor: " + ctx.executor());

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

  @Override
  protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) {
    logger.debug("extract msg start, ctx: " + ctx + " ctx executor: " + ctx.executor());

    try {
      Message message = messageSupplier.get();
      message.extract(msg);
      message.setSeqNo(id.nextStrSeqNo());
      message.print();
      out.add(message);
    } catch (Exception e) {
      logger.warn("**************************");
      logger.warn("extract message failed!!!", e);
      logger.warn("**************************");
      logger.error(String.format("extract message failed, msg: %s", ISOUtil.byte2hex(msg)));
    }
  }
}

package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;
import me.card.switchv1.component.Prefix;
import me.card.switchv1.core.handler.event.EchoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// can not share
public class StreamHandler extends ByteToMessageCodec<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(StreamHandler.class);
  public static final String NAME = "StreamHandler";

  private final Prefix prefix;

  public StreamHandler(Prefix prefix) {
    this.prefix = prefix;
  }


  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
    logger.debug("[stage {}] encode start: thread={}", NAME, Thread.currentThread().getName());

    if (logger.isDebugEnabled()) {
      logger.debug("return encode hex msg: {}", ByteBufUtil.hexDump(msg));
    }

    try {
      if (msg.array().length != prefix.getPrefixLength()) {
        byte[] byteLength = prefix.getBytePrefix(msg.array().length);
        out.writeBytes(byteLength);
      }
      out.writeBytes(msg);
    } catch (Exception e) {
      throw new StreamHandlerException("encode error: " + e.getMessage());
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
    logger.debug("************ new income tran received ************");
    logger.debug("[stage {}] decode start: thread={}", NAME, Thread.currentThread().getName());

    if (logger.isDebugEnabled()) {
      logger.debug("decode byteBuf hex: {} ", ByteBufUtil.hexDump(byteBuf));
    }

    int prefixLength = prefix.getPrefixLength();

    while (true) {
      if (byteBuf.readableBytes() < prefixLength) {
        return;
      }
      byteBuf.markReaderIndex();

      ByteBuf byteLen = Unpooled.buffer(prefixLength);
      byteBuf.readBytes(byteLen);
      int intLen = prefix.getIntPrefix(byteLen.array());

      // echo message
      if (intLen == 0) {
        logger.debug("echo msg");
        //not need to release byte buf
        ctx.fireUserEventTriggered(new EchoEvent());
        return;
      }
      //

      if (byteBuf.readableBytes() < intLen) {
        logger.debug("readable bytes not enough");
        byteBuf.resetReaderIndex();
        return;
      }


      ByteBuf byteMsg = Unpooled.unreleasableBuffer(Unpooled.buffer(intLen));
      byteBuf.readBytes(byteMsg);

      out.add(byteMsg);

      if (logger.isDebugEnabled()) {
        logger.debug("split finished, msg hex: {} ", ByteBufUtil.hexDump(byteMsg));
      }
    }
  }

}

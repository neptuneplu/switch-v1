package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
      if (msg.readableBytes() != prefix.getPrefixLength()) {
        byte[] byteLength = prefix.getBytePrefix(msg.readableBytes());
        out.writeBytes(byteLength);
      }
      out.writeBytes(msg);
    } catch (Exception e) {
      logger.error("write byte buf error!!!", e);
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

      byte[] byteLen = new byte[prefixLength];
      byteBuf.readBytes(byteLen);
      int intLen = prefix.getIntPrefix(byteLen);

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


      ByteBuf byteMsg = byteBuf.retainedSlice(byteBuf.readerIndex(), intLen);
      byteBuf.skipBytes(intLen);

      out.add(byteMsg);

      if (logger.isDebugEnabled()) {
        logger.debug("split finished, msg hex: {} ", ByteBufUtil.hexDump(byteMsg));
      }
    }
  }

}

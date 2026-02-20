package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;
import me.card.switchv1.core.component.Prefix;
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
    logger.debug("************ new income tran received, byte encode start ************");

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("return encode hex msg: %s", ByteBufUtil.hexDump(msg)));
    }

    try {
      if (msg.array().length != prefix.getPrefixLength()) {
        byte[] byteLength = prefix.getBytePrefix(msg.array().length);
        out.writeBytes(byteLength);
      }
      out.writeBytes(msg);
    } catch (Exception e) {
      logger.error("write byte buf error!!!", e);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
    logger.debug("byte decode start");

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("decode byteBuf hex: %s ", ByteBufUtil.hexDump(byteBuf)));
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
        logger.debug(String.format("split finished, msg hex: %s ", ByteBufUtil.hexDump(byteMsg)));
      }
    }
  }

}

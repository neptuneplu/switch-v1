package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import me.card.switchv1.core.component.Prefix;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamHandler extends ByteToMessageCodec<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(StreamHandler.class);

  private final Prefix prefix;

  public StreamHandler(Prefix prefix) {
    this.prefix = prefix;
  }


  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {

    logger.debug("byte encode start");


    if (logger.isDebugEnabled()) {
      logger.debug(String.format("return encode hex msg: %s", ISOUtil.byte2hex(msg)));
    }

    try {
      if (msg.length != prefix.getPrefixLength()) {
        byte[] byteLength = prefix.getBytePrefix(msg.length);
        out.writeBytes(byteLength);
      }
      out.writeBytes(msg);
    } catch (Exception e) {
      logger.error("**************************");
      logger.error("write byte buf error!!!", e);
      logger.error("**************************");
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
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

      byte[] byteLen = new byte[prefixLength];
      byteBuf.readBytes(byteLen);
      int intLen = prefix.getIntPrefix(byteLen);

      // discard echo message
      if (intLen == 0) {
        logger.debug("echo msg");
        ReferenceCountUtil.release(byteBuf);
        return;
      }
      //

      if (byteBuf.readableBytes() < intLen) {
        logger.debug("readable bytes not enough");
        byteBuf.resetReaderIndex();
        return;
      }

      byte[] byteMsg = new byte[intLen];
      byteBuf.readBytes(byteMsg);
      out.add(byteMsg);

      if (logger.isDebugEnabled()) {
        logger.debug(String.format("split finished, msg hex: %s ", ISOUtil.byte2hex(byteMsg)));
      }
    }
  }

}

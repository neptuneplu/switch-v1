package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import me.card.switchv1.core.component.Prefix;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamInputHandler extends ByteToMessageDecoder {
  private static final Logger logger = LoggerFactory.getLogger(StreamInputHandler.class);

  private final Prefix prefix;

  public StreamInputHandler(Prefix prefix) {
    this.prefix = prefix;
  }


  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
                        List<Object> list) {
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
      list.add(byteMsg);

      if (logger.isDebugEnabled()) {
        logger.debug(String.format("split finished, msg hex: %s ", ISOUtil.byte2hex(byteMsg)));
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("**************************");
    logger.error("read byte buf error!!!", cause);
    logger.error("**************************");
  }

}

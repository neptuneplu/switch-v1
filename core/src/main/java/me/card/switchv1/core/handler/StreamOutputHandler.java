package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.card.switchv1.core.component.Prefix;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamOutputHandler extends MessageToByteEncoder<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(StreamOutputHandler.class);

  private final Prefix prefix;

  public StreamOutputHandler(Prefix prefix) {
    this.prefix = prefix;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] byteMsg, ByteBuf byteBuf) {
    logger.debug("byte encode start");
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("return encode hex msg: %s", ISOUtil.byte2hex(byteMsg)));
    }

    try {
      if (byteMsg.length != prefix.getPrefixLength()) {
        byte[] byteLength = prefix.getBytePrefix(byteMsg.length);
        byteBuf.writeBytes(byteLength);
      }
      byteBuf.writeBytes(byteMsg);
    } catch (Exception e) {
      logger.error("**************************");
      logger.error("write byte buf error!!!", e);
      logger.error("**************************");
    }

  }

}

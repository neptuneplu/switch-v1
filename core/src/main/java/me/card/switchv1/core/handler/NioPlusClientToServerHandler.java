package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioPlusClientToServerHandler extends SimpleChannelInboundHandler<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(NioPlusClientToServerHandler.class);

  private final ChannelHandlerContext serverCtx;

  public NioPlusClientToServerHandler(ChannelHandlerContext serverCtx) {
    this.serverCtx = serverCtx;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) {
    logger.debug("NioPlusClientToServerHandler start");
    //write to server channel
    serverCtx.writeAndFlush(msg);
  }
}

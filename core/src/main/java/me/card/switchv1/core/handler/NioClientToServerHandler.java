package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioClientToServerHandler extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(NioClientToServerHandler.class);

  private final ChannelHandlerContext serverCtx;

  public NioClientToServerHandler(ChannelHandlerContext serverCtx) {
    this.serverCtx = serverCtx;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api api) {
    logger.debug("NioClientToServerHandler start");
    //write to server channel
    serverCtx.writeAndFlush(api);
  }
}

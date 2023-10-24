package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.client.BackOfficeClientBio;
import me.card.switchv1.core.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerBio extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerBio.class);
  public static final String NAME = "BackOfficeHandlerBio";

  private final BackOfficeClientBio backOfficeClientBio;

  public BackOfficeHandlerBio(BackOfficeClientBio backOfficeClientBio) {
    this.backOfficeClientBio = backOfficeClientBio;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Api api) {
    logger.debug("BackOfficeHandlerBio channelRead0 start");

    Api responseApi = backOfficeClientBio.send(api);
    channelHandlerContext.channel().writeAndFlush(responseApi);
  }

}

package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.client.BackOfficeClientNio;
import me.card.switchv1.core.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerNio extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerNio.class);

  private final BackOfficeClientNio backOfficeClientNio;

  public BackOfficeHandlerNio(BackOfficeClientNio backOfficeClientNio) {
    this.backOfficeClientNio = backOfficeClientNio;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api api) {
    logger.debug("BackOfficeHandlerNio channelRead0 start");
    try {
      backOfficeClientNio.send(ctx, api);
    } catch (Exception e) {
      logger.error("get http request error", e);
      throw new HandlerException("get http request error");
    }
  }

}

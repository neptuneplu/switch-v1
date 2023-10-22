package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.client.BackOfficeClientNioPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerNioPlus extends SimpleChannelInboundHandler<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerNioPlus.class);

  private final BackOfficeClientNioPlus backOfficeClientNioPlus;

  public BackOfficeHandlerNioPlus(BackOfficeClientNioPlus backOfficeClientNioPlus) {
    this.backOfficeClientNioPlus = backOfficeClientNioPlus;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
    logger.debug(
        "BackOfficeHandlerNioPlus read start, ctx: " + ctx + " ctx executor: " + ctx.executor());

    try {
      backOfficeClientNioPlus.send(ctx, msg);
    } catch (Exception e) {
      logger.error("get http request error", e);
      throw new HandlerException("get http request error");
    }
  }
}

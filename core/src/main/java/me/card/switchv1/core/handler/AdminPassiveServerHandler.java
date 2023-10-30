package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.server.Queryable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminPassiveServerHandler extends AdminHandler {
  private static final Logger logger = LoggerFactory.getLogger(AdminPassiveServerHandler.class);

  private final Queryable queryable;

  public AdminPassiveServerHandler(HeartBeat heartBeat, Queryable queryable) {
    super(heartBeat);
    this.queryable = queryable;
  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("channelActive start, channel: %s", ctx.channel().toString()));
    }
    queryable.setupChannel(ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    logger.warn("channelInActive!!!!!!!");
  }

  @Override
  public void channelUnregistered(final ChannelHandlerContext ctx) {
    logger.warn("channelUnregistered");
  }


}

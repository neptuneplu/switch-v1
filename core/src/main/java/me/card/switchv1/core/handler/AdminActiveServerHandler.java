package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import java.util.concurrent.TimeUnit;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.connector.AutoConnectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminActiveServerHandler extends AdminHandler {
  private static final Logger logger = LoggerFactory.getLogger(AdminActiveServerHandler.class);

  private static final AttributeKey<Boolean> ON_LINE_FLAG = AttributeKey.valueOf("ON_LINE");

  private final AutoConnectable autoConnectable;

  public AdminActiveServerHandler(HeartBeat heartBeat, AutoConnectable autoConnectable) {
    super(heartBeat);
    this.autoConnectable = autoConnectable;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("channelActive start, channel: %s", ctx.channel().toString()));
    }
    autoConnectable.setupChannel(ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    logger.warn("channelInActive!!!!!!!");
  }

  @Override
  public void channelUnregistered(final ChannelHandlerContext ctx) {
    logger.warn("channelUnregistered");

    if (Boolean.TRUE.equals(ctx.channel().attr(ON_LINE_FLAG).get())) {
      ctx.channel().eventLoop().schedule(this::reconnect, 3, TimeUnit.SECONDS);
    }
  }

  private void reconnect() {
    autoConnectable.connect();
  }


}

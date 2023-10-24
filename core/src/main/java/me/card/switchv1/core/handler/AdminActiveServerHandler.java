package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import java.util.concurrent.TimeUnit;
import me.card.switchv1.core.component.HeartBeat;
import me.card.switchv1.core.server.Reconnectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminActiveServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AdminActiveServerHandler.class);

  private static final AttributeKey<Boolean> ON_LINE_FLAG = AttributeKey.valueOf("ON_LINE");

  private final HeartBeat heartBeat;
  private final Reconnectable reconnectable;
  private int idleCount;

  public AdminActiveServerHandler(HeartBeat heartBeat, Reconnectable reconnectable) {
    this.heartBeat = heartBeat;
    this.reconnectable = reconnectable;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("channelActive start, channel: %s", ctx.channel().toString()));
    }
    reconnectable.setChannel(ctx.channel());
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
    reconnectable.connect();
  }


  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
    if (!(evt instanceof IdleStateEvent)) {
      return;
    }

    IdleStateEvent e = (IdleStateEvent) evt;
    if (e.state() == IdleState.READER_IDLE) {
      idleCount++;
      if (idleCount == 2) {
        if (heartBeat.isRequireHeartBeat()) {
          logger.debug("send heart beat msg");
          ctx.writeAndFlush(heartBeat.getHeartBeatCode());
        }
        return;
      }
      if (idleCount == 3) {
        idleCount = 0;
        ctx.close();
      }

    }
  }




}

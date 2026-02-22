package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import me.card.switchv1.component.HeartBeat;
import me.card.switchv1.core.handler.event.EchoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AdminHandler.class);

  private int idleCount;
  private final HeartBeat heartBeat;

  public AdminHandler(HeartBeat heartBeat) {
    this.heartBeat = heartBeat;
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("exception caught", cause);
  }


  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
    if (evt instanceof IdleStateEvent) {
      idleEvent(ctx, evt);
      return;
    }

    if (evt instanceof EchoEvent) {
      echoEvent(ctx, evt);
    }

  }

  private void idleEvent(ChannelHandlerContext ctx, Object evt) {
    logger.debug("idle event start");
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

  private void echoEvent(ChannelHandlerContext ctx, Object evt) {
    logger.debug("echo event start");
    //noop
  }
}

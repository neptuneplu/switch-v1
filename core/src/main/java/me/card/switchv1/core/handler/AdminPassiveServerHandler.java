package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import me.card.switchv1.core.component.HeartBeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminPassiveServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AdminPassiveServerHandler.class);

  private final HeartBeat heartBeat;
  private int idleCount;

  public AdminPassiveServerHandler(HeartBeat heartBeat) {
    this.heartBeat = heartBeat;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    logger.info("channelActive start");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    logger.warn("channelInActive!!!!!!!");
  }

  @Override
  public void channelUnregistered(final ChannelHandlerContext ctx) {
    logger.warn("channelUnregistered");
    ctx.close();
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

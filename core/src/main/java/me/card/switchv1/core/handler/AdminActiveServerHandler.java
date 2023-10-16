package me.card.switchv1.core.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.TimeUnit;
import me.card.switchv1.core.component.HeartBeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminActiveServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AdminActiveServerHandler.class);

  private final HeartBeat heartBeat;
  private final Bootstrap bootstrap;
  private int idleCount;

  public AdminActiveServerHandler(HeartBeat heartBeat, Bootstrap bootstrap) {
    this.heartBeat = heartBeat;
    this.bootstrap = bootstrap;
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
    ctx.channel().eventLoop().schedule(() -> {
      logger.warn("channelUnregistered Reconnecting");
      try {
        bootstrap.connect().sync();
      } catch (Exception e) {
        logger.error("reconnect error");
      }
    }, 3, TimeUnit.SECONDS);
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

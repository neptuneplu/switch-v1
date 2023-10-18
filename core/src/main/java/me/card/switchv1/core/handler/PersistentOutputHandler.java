package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentOutputHandler extends ChannelOutboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(PersistentOutputHandler.class);


  private final PersistentWorker persistentWorker;
  private final EventLoopGroup persistentEventLoopGroup;

  public PersistentOutputHandler(PersistentWorker persistentWorker,
                                 EventLoopGroup persistentEventLoopGroup) {
    this.persistentWorker = persistentWorker;
    this.persistentEventLoopGroup = persistentEventLoopGroup;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    logger.debug("persistent output start");

    persistentEventLoopGroup.submit(
        () -> {
          try {
            persistentWorker.saveOutput((byte[]) msg);
          } catch (Exception e) {
            logger.error("persistent output message error", e);
          }
        });

    ctx.writeAndFlush(msg);
  }
}

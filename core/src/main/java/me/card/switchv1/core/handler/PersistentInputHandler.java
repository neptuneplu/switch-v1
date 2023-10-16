package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentInputHandler extends SimpleChannelInboundHandler<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(PersistentInputHandler.class);

  private final PersistentWorker persistentWorker;
  private final EventLoopGroup persistentEventLoopGroup;

  public PersistentInputHandler(PersistentWorker persistentWorker,
                                EventLoopGroup persistentEventLoopGroup) {
    this.persistentWorker = persistentWorker;
    this.persistentEventLoopGroup = persistentEventLoopGroup;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) {
    logger.debug("persistent input start");

    persistentEventLoopGroup.submit(
        () -> {
          try {
            persistentWorker.saveInput(bytes);
          } catch (Exception e) {
            logger.error("persistent input message error", e);
          }
        });

    //todo check
    channelHandlerContext.fireChannelRead(bytes);
  }
}

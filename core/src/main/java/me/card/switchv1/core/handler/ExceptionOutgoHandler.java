package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionOutgoHandler  extends ChannelOutboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(ExceptionOutgoHandler.class);
  public static final String NAME = "ExceptionOutgoHandler";

  private final Processor processor;

  public ExceptionOutgoHandler(Processor processor) {
    this.processor = processor;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Channel {} exception", ctx.channel().id(), cause);




  }
}

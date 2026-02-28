package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.card.switchv1.component.PersistentWorker;
import me.card.switchv1.core.handler.attributes.ChannelAttributes;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionIncomeHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(ExceptionIncomeHandler.class);
  public static final String NAME = "ExceptionIncomeHandler";

  private final Processor processor;

  public ExceptionIncomeHandler(Processor processor) {
    this.processor = processor;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Channel {} exception", ctx.channel().id(), cause);

    if (cause instanceof ApiHandlerException) {
      processor.saveIncomeMessage(ctx.channel().attr(ChannelAttributes.MESSAGE).get());
    }


  }
}

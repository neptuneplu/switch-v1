package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageContext;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ProcessHandler extends SimpleChannelInboundHandler<Message> {
  private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
  public static final String NAME = "ProcessHandler";

  private final Processor processor;

  public ProcessHandler(Processor processor) {
    this.processor = processor;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
    logger.debug("[stage *BackOfficeHandler] Netty received: thread={}, message={}",
        Thread.currentThread().getName(), msg);

    MessageContext context = new MessageContext(ctx.channel());
    context.setIncomeMsg(msg);
    processor.handleIncomeAsync(context);
  }
}

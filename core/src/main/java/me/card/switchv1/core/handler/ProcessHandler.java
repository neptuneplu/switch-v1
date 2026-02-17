package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.RequestContext;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ProcessHandler extends SimpleChannelInboundHandler<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
  public static final String NAME = "ProcessHandler";

  private final Processor processor;

  public ProcessHandler(Processor processor) {
    this.processor = processor;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
    logger.debug("[stage *BackOfficeHandler] Netty received: thread={}, rawMessage={}",
        Thread.currentThread().getName(), msg);

    RequestContext context = new RequestContext(ctx, msg);

    processor.processRequest(context);
  }
}

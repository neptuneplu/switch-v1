package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.RequestContext;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ProcessHandler extends SimpleChannelInboundHandler<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
  public static final String NAME = "BackOfficeHandler";

  private final Processor processor;
  private final DestinationURL destinationURL;

  public ProcessHandler(Processor processor, DestinationURL destinationURL) {
    this.processor = processor;
    this.destinationURL = destinationURL;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
    logger.debug("[stage *BackOfficeHandler] Netty received: thread={}, rawMessage={}",
        Thread.currentThread().getName(), msg);

    RequestContext context = new RequestContext(ctx, msg);
    context.setDestinationURL(destinationURL);

    processor.processRequest(context);
  }
}

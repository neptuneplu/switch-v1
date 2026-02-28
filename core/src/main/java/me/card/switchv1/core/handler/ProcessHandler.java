package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ProcessHandler extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
  public static final String NAME = "ProcessHandler";

  private final Processor processor;

  public ProcessHandler(Processor processor) {
    this.processor = processor;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api api) {
    logger.debug("[stage {}] channelRead0 start: thread={}", NAME,
        Thread.currentThread().getName());

    if (api.isRequest()) {
      processor.handleIncomeRequest(api);
    } else if (api.isResponse()) {
      processor.handleIncomeResponse(api);
    } else {
      logger.error("MTI error");
    }
  }
}

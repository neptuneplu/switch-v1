package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.component.Message;
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
    logger.debug("[stage {}] channelRead0 start: thread={}", NAME,
        Thread.currentThread().getName());

    if (msg.isRequest()) {
      processor.handleIncomeRequestAsync(msg);
    } else if (msg.isResponse()) {
      processor.handleIncomeResponseAsync(msg);
    } else {
      logger.error("message MTI error");
    }
  }
}

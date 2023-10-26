package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioClientActionHandler extends SimpleChannelInboundHandler<Object> {
  private static final Logger logger = LoggerFactory.getLogger(NioClientActionHandler.class);

  private final Consumer<Object> action;

  public NioClientActionHandler(Consumer<Object> action) {
    this.action = action;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (Objects.nonNull(action)) {
      action.accept(msg);
      ctx.close().addListener(f -> logger.debug("backoffice connection closed"));
    }
  }
}

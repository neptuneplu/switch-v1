package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioClientActionHandler extends SimpleChannelInboundHandler<Object> {
  private static final Logger logger = LoggerFactory.getLogger(NioClientActionHandler.class);

  private final Promise<Object> promise;

  public NioClientActionHandler(Promise<Object> promise) {
    this.promise = promise;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (Objects.nonNull(promise)) {
      promise.setSuccess(msg);
      ctx.close().addListener(f -> logger.debug("backoffice connection closed"));
    }
  }
}

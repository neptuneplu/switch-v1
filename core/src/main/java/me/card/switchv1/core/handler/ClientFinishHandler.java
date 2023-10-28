package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import me.card.switchv1.core.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFinishHandler extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(ClientFinishHandler.class);

  private final Promise<Api> promise;

  public ClientFinishHandler(Promise<Api> promise) {
    this.promise = promise;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api msg) {
    if (Objects.nonNull(promise)) {
      promise.setSuccess(msg);
      ctx.close().addListener(f -> logger.debug("backoffice connection closed"));
    }
  }
}

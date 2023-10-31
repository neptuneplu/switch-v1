package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFinishHandlerPlus extends SimpleChannelInboundHandler<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(ClientFinishHandlerPlus.class);

  private final Promise<ByteBuf> promise;

  public ClientFinishHandlerPlus(Promise<ByteBuf> promise) {
    this.promise = promise;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
    if (Objects.nonNull(promise)) {
      promise.setSuccess(msg);
      ctx.close().addListener(f -> logger.debug("backoffice connection closed"));
    } else {
      throw new HandlerException("promise is null");
    }
  }
}

package me.card.switchv1.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class BackOfficeHandlerPlus extends SimpleChannelInboundHandler<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerPlus.class);
  public static final String NAME = "BackOfficeHandlerNioPlus";

  private final Client<ByteBuf> client;

  public BackOfficeHandlerPlus(Client<ByteBuf> client) {
    this.client = client;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
    logger.debug("BackOfficeHandlerNioPlus read start");

    Promise<ByteBuf> promise = new DefaultPromise<ByteBuf>(ctx.executor())
        .addListener(future -> ctx.writeAndFlush(future.get()));

    try {
      client.sendAsync(promise, (EventLoop) ctx.executor(), msg);
    } catch (Exception e) {
      logger.error("get http request error", e);
      throw new HandlerException("get http request error");
    }
  }
}

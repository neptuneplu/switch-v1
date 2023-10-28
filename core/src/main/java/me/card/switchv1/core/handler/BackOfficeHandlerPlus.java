package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerPlus extends SimpleChannelInboundHandler<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerPlus.class);
  public static final String NAME = "BackOfficeHandlerNioPlus";

  private final Client<byte[]> client;

  public BackOfficeHandlerPlus(Client<byte[]> client) {
    this.client = client;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) {
    logger.debug("BackOfficeHandlerNioPlus read start");

    Promise<byte[]> promise = new DefaultPromise<byte[]>(ctx.executor()).addListener(future -> {
      ctx.writeAndFlush(future.get());
    });

    try {
      client.sendAsync(promise, (EventLoop) ctx.executor(), msg);
    } catch (Exception e) {
      logger.error("get http request error", e);
      throw new HandlerException("get http request error");
    }
  }
}

package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.client.Client;
import me.card.switchv1.core.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandler extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandler.class);
  public static final String NAME = "BackOfficeHandlerNio";

  private final Client<Api> client;

  public BackOfficeHandler(Client<Api> client) {
    this.client = client;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api api) {
    logger.debug("BackOfficeHandlerNio channelRead0 start");

    Promise<Api> promise = new DefaultPromise<Api>(ctx.executor()).addListener(future -> {
      ctx.writeAndFlush(future.get());
    });

    try {
      client.sendAsync(promise, (EventLoop) ctx.executor(), api);
    } catch (Exception e) {
      logger.error("get http request error", e);
      throw new HandlerException("get http request error");
    }


  }
}

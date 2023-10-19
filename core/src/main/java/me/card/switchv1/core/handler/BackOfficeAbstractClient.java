package me.card.switchv1.core.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BackOfficeAbstractClient {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeAbstractClient.class);

  protected final DestinationURL destinationURL;
  protected final Class<? extends Api> responseApiClz;
  protected final EventLoopGroup sendEventLoopGroup;

  protected BackOfficeAbstractClient(DestinationURL destinationURL,
                                     Class<? extends Api> responseApiClz,
                                     EventLoopGroup sendEventLoopGroup) {
    this.destinationURL = destinationURL;
    this.responseApiClz = responseApiClz;
    this.sendEventLoopGroup = sendEventLoopGroup;
  }

  // should not public
  public <T> void send(ChannelHandlerContext ctx, T t) {
    logger.debug("send to backoffice bootstrap start");

    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group((EventLoopGroup) ctx.executor())
        .channel(NioSocketChannel.class)
        .handler(getChannelInitializer(ctx));

    ChannelFuture future;

    try {
      future = bootstrap.connect(destinationURL.getDestinationAddress()).sync();
    } catch (Exception e) {
      logger.error("connect backoffice error", e);
      throw new HandlerException("connect backoffice error");
    }

    future.channel().writeAndFlush(t);
    logger.debug("send to backoffice bootstrap end");

  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      ChannelHandlerContext ctx);
}

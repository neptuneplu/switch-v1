package me.card.switchv1.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BackOfficeAbstractClientNio extends BackOfficeAbstractClient {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeAbstractClientNio.class);

  protected BackOfficeAbstractClientNio(DestinationURL destinationURL,
                                        Class<? extends Api> responseApiClz) {
    super(destinationURL, responseApiClz);
  }

  public <T> void sendAsync(ChannelHandlerContext ctx, T t) {
    logger.debug("sendSync to backoffice bootstrap start");

    Promise<Object> promise = new DefaultPromise<>(ctx.executor()).addListener(future -> {
      ctx.writeAndFlush(future.get());
    });

    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group((EventLoopGroup) ctx.executor())
        .channel(NioSocketChannel.class)
        //todo create too much handlers
        .handler(getChannelInitializer(promise));


    bootstrap.connect(destinationURL.getDestinationAddress()).addListener(
        (ChannelFutureListener) future -> future.channel().writeAndFlush(t));

  }


  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      Promise<Object> promise);
}

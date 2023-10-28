package me.card.switchv1.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BackOfficeAbstractClient<T> implements Client<T> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeAbstractClient.class);

  protected DestinationURL destinationURL;
  protected Class<? extends Api> responseApiClz;

  @Override
  public void sendAsync(Promise<T> promise, EventLoop eventLoop, T t) {
    logger.debug("sendSync to backoffice bootstrap start");

    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(eventLoop)
        .channel(NioSocketChannel.class)
        //todo create too much handlers
        .handler(getChannelInitializer(promise));

    bootstrap.connect(destinationURL.getDestinationAddress()).addListener(
        (ChannelFutureListener) future -> future.channel().writeAndFlush(t));
  }

  public BackOfficeAbstractClient<T> destinationURL(
      DestinationURL destinationURL) {
    this.destinationURL = destinationURL;
    return this;
  }

  public BackOfficeAbstractClient<T> responseApiClz(
      Class<? extends Api> responseApiClz) {
    this.responseApiClz = responseApiClz;
    return this;
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      Promise<T> promise);
}

package me.card.switchv1.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.handler.BackOfficeHttpRequestHandler;
import me.card.switchv1.core.handler.BackOfficeHttpResponseHandler;
import me.card.switchv1.core.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BackOfficeAbstractClient<T> implements Client<T> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeAbstractClient.class);

  protected DestinationURL destinationURL;
  protected Class<? extends Api> responseApiClz;
  protected BackOfficeHttpResponseHandler backOfficeHttpResponseHandler;
  protected BackOfficeHttpRequestHandler backOfficeHttpRequestHandler;

  @Override
  public void sendAsync(Promise<T> promise, EventLoop eventLoop, T t) {
    logger.debug("sendSync to backoffice bootstrap start");

    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(eventLoop)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
        .handler(getChannelInitializer(promise));

    bootstrap.connect(destinationURL.getDestinationAddress()).addListener(future -> {
      if (future.isSuccess()) {
        ((ChannelFuture) future).channel().writeAndFlush(t);
      } else {
        logger.error("backoffice not connected", future.cause());
        throw new ClientException("connect error");
      }
    });
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

  protected void init0() {
    initCheck0();
    backOfficeHttpResponseHandler = new BackOfficeHttpResponseHandler(responseApiClz);
    backOfficeHttpRequestHandler = new BackOfficeHttpRequestHandler(destinationURL);
  }

  private void initCheck0() {
    Assert.notNull(responseApiClz, "init error, responseApiClz is null");
    Assert.notNull(destinationURL, "init error, destinationURL is null");
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      Promise<T> promise);
}

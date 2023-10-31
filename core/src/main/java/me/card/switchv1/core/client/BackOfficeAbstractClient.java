package me.card.switchv1.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.handler.BackOfficeHttpRequestHandler;
import me.card.switchv1.core.handler.BackOfficeHttpResponseHandler;
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
        .handler(getChannelInitializer(promise));

    bootstrap.connect(destinationURL.getDestinationAddress()).addListener(
        (ChannelFutureListener) future -> {
          future.channel().writeAndFlush(t);
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
    if (Objects.isNull(responseApiClz)) {
      throw new ClientException("init error, responseApiClz is null");
    }
    if (Objects.isNull(destinationURL)) {
      throw new ClientException("init error, destinationURL is null");
    }
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      Promise<T> promise);
}

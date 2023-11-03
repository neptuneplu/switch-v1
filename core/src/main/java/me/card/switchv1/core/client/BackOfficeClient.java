package me.card.switchv1.core.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.TimeUnit;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.handler.ClientFinishHandler;

public class BackOfficeClient extends BackOfficeAbstractClient<Api> {

  public ChannelInitializer<SocketChannel> getChannelInitializer(Promise<Api> promise) {
    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpClientCodec()) //dup
            .addLast(new HttpObjectAggregator(10 * 1024 * 1024)) //dup
            .addLast(backOfficeHttpResponseHandler) //in
            .addLast(backOfficeHttpRequestHandler) //out
            .addLast(new ClientFinishHandler(promise)) //in
            .addLast(new ReadTimeoutHandler(2, TimeUnit.SECONDS))
        ;
      }
    };
  }

  @Override
  public BackOfficeClient init() {
    super.init0();
    return this;
  }

}
package me.card.switchv1.core.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.handler.BackOfficeHttpRequestHandler;
import me.card.switchv1.core.handler.BackOfficeHttpResponseHandler;
import me.card.switchv1.core.handler.NioClientActionHandler;

public class BackOfficeClientNio extends BackOfficeAbstractClientNio {

  public BackOfficeClientNio(DestinationURL destinationURL, Class<? extends Api> responseApiClz) {
    super(destinationURL, responseApiClz);
  }


  public ChannelInitializer<SocketChannel> getChannelInitializer(Promise<Object> promise) {
    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpClientCodec()) //dup
            .addLast(new HttpObjectAggregator(10 * 1024 * 1024)) //dup
            .addLast(new BackOfficeHttpResponseHandler(responseApiClz)) //in
            .addLast(new BackOfficeHttpRequestHandler(destinationURL)) //out
            .addLast(new NioClientActionHandler(promise)); //in
      }
    };
  }
}
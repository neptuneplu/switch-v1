package me.card.switchv1.core.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.handler.BackOfficeHttpRequestHandler;
import me.card.switchv1.core.handler.BackOfficeHttpResponseHandler;
import me.card.switchv1.core.handler.NioClientToServerHandler;

public class BackOfficeClientNio extends BackOfficeAbstractClientNio {

  public BackOfficeClientNio(DestinationURL destinationURL, Class<? extends Api> responseApiClz) {
    super(destinationURL, responseApiClz);
  }

  public ChannelInitializer<SocketChannel> getChannelInitializer(ChannelHandlerContext ctx) {
    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast(new HttpClientCodec()); //dup
        ph.addLast(new HttpObjectAggregator(10 * 1024 * 1024)); //dup
        ph.addLast(new BackOfficeHttpResponseHandler(responseApiClz)); //in
        ph.addLast(new NioClientToServerHandler(ctx)); //in
        ph.addLast(new BackOfficeHttpRequestHandler(destinationURL)); //out
      }
    };
  }
}
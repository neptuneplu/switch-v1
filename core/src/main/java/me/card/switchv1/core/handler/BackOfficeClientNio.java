package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;

public class BackOfficeClientNio extends BackOfficeAbstractClient {

  public BackOfficeClientNio(DestinationURL destinationURL,
                             Class<? extends Api> responseApiClz,
                             EventLoopGroup sendEventLoopGroup) {
    super(destinationURL, responseApiClz, sendEventLoopGroup);
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
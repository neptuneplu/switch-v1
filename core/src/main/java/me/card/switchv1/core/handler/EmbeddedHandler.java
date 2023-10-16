package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;

public class EmbeddedHandler extends SimpleChannelInboundHandler<byte[]> {
  private static final EventLoopGroup eventLoopGroup = new DefaultEventLoopGroup(8);
  private final ChannelInitializer<EmbeddedChannel> channelInitializer;


  public EmbeddedHandler(ChannelInitializer<EmbeddedChannel> channelInitializer) {
    this.channelInitializer = channelInitializer;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) {
    eventLoopGroup.submit(() -> {
      System.out.println("thread id: " + Thread.currentThread().getId());
      EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);
      channel.writeInbound(msg);
      byte[] responseByteMsg = channel.readOutbound();
      ctx.writeAndFlush(responseByteMsg);
    });
  }
}

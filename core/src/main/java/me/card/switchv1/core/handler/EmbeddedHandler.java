package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;

public class EmbeddedHandler extends SimpleChannelInboundHandler<byte[]> {

  private final EventLoopGroup sendNioEventLoopGroup;

  private final ChannelInitializer<EmbeddedChannel> channelInitializer;


  public EmbeddedHandler(EventLoopGroup sendNioEventLoopGroup,
                         ChannelInitializer<EmbeddedChannel> channelInitializer) {
    this.sendNioEventLoopGroup = sendNioEventLoopGroup;
    this.channelInitializer = channelInitializer;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) {
    sendNioEventLoopGroup.submit(() -> {
      EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);
      channel.writeInbound(msg);
      byte[] responseByteMsg = channel.readOutbound();
      ctx.writeAndFlush(responseByteMsg);
    });
  }
}

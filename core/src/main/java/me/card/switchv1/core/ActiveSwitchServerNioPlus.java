package me.card.switchv1.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerNioPlus;
import me.card.switchv1.core.handler.StreamInputHandler;
import me.card.switchv1.core.handler.StreamOutputHandler;

public class ActiveSwitchServerNioPlus extends AbstractActiveSwitchServer {

  @Override
  protected ChannelInitializer<SocketChannel> channelInitializer(
      EventLoopGroup persistentEventLoopGroup,
      EventLoopGroup sendEventLoopGroup,
      Bootstrap bootstrap) {

    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast(new StreamInputHandler(prefix));
        ph.addLast(new StreamOutputHandler(prefix));
        ph.addLast(
            new BackOfficeHandlerNioPlus(destinationURL, responseApiClz, sendEventLoopGroup,
                messageSupplier, apiCoder, persistentWorker, persistentEventLoopGroup));
        ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
        ph.addLast(new AdminActiveServerHandler(heartBeat, bootstrap));
      }
    };
  }
}

package me.card.switchv1.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiCodecHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerBio;
import me.card.switchv1.core.handler.EmbeddedHandler;
import me.card.switchv1.core.handler.MessageHandler;
import me.card.switchv1.core.handler.PersistentHandler;
import me.card.switchv1.core.handler.StreamHandler;

public class ActiveSwitchServerBioPlus extends AbstractActiveSwitchServer {

  @Override
  protected ChannelInitializer<SocketChannel> channelInitializer(
      EventLoopGroup persistentEventLoopGroup,
      EventLoopGroup sendEventLoopGroup,
      Bootstrap bootstrap) {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast(new StreamHandler(prefix));
        ph.addLast(new EmbeddedHandler(sendEventLoopGroup, new ChannelInitializer<>() {
          @Override
          protected void initChannel(EmbeddedChannel ch) {
            ChannelPipeline ph = ch.pipeline();
            ph.addLast(new PersistentHandler(persistentWorker, persistentEventLoopGroup));
            ph.addLast(new MessageHandler(messageSupplier));
            ph.addLast(new ApiCodecHandler(apiCoder));
            ph.addLast(new BackOfficeHandlerBio(destinationURL, responseApiClz, null));
          }
        }));
        ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
        ph.addLast(new AdminActiveServerHandler(heartBeat, bootstrap));
      }
    };
  }


}

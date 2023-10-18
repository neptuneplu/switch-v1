package me.card.switchv1.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiDecodeHandler;
import me.card.switchv1.core.handler.ApiEncodeHandler;
import me.card.switchv1.core.handler.BackofficeHandlerByRest;
import me.card.switchv1.core.handler.EmbeddedHandler;
import me.card.switchv1.core.handler.MessageCompressHandler;
import me.card.switchv1.core.handler.MessageExtractHandler;
import me.card.switchv1.core.handler.PersistentInputHandler;
import me.card.switchv1.core.handler.PersistentOutputHandler;
import me.card.switchv1.core.handler.StreamInputHandler;
import me.card.switchv1.core.handler.StreamOutputHandler;

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
        ph.addLast(new StreamInputHandler(prefix));
        ph.addLast(new StreamOutputHandler(prefix));
        ph.addLast(new EmbeddedHandler(sendEventLoopGroup, new ChannelInitializer<>() {
          @Override
          protected void initChannel(EmbeddedChannel ch) {
            ChannelPipeline ph = ch.pipeline();
            ph.addLast(new PersistentInputHandler(persistentWorker, persistentEventLoopGroup));
            ph.addLast(new PersistentOutputHandler(persistentWorker, persistentEventLoopGroup));
            ph.addLast(new MessageExtractHandler(messageSupplier));
            ph.addLast(new MessageCompressHandler());
            ph.addLast(new ApiDecodeHandler(apiCoder));
            ph.addLast(new ApiEncodeHandler(apiCoder));
            ph.addLast(new BackofficeHandlerByRest(destinationURL, responseApiClz, null));
          }
        }));
        ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
        ph.addLast(new AdminActiveServerHandler(heartBeat, bootstrap));
      }
    };
  }


}

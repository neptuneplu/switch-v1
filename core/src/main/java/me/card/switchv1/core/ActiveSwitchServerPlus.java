package me.card.switchv1.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiDecodeHandler;
import me.card.switchv1.core.handler.ApiEncodeHandler;
import me.card.switchv1.core.handler.BackofficeHandlerByRestPlus;
import me.card.switchv1.core.handler.EmbeddedHandler;
import me.card.switchv1.core.handler.MessageCompressHandler;
import me.card.switchv1.core.handler.MessageExtractHandler;
import me.card.switchv1.core.handler.PersistentInputHandler;
import me.card.switchv1.core.handler.PersistentOutputHandler;
import me.card.switchv1.core.handler.StreamInputHandler;
import me.card.switchv1.core.handler.StreamOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveSwitchServerPlus extends AbstractSwitchServer implements SwitchServer {
  private static final Logger logger = LoggerFactory.getLogger(ActiveSwitchServerPlus.class);

  private final Bootstrap bootstrap = new Bootstrap();
  private final EventLoopGroup serverNioEventLoopGroup = new NioEventLoopGroup(1,
      new DefaultThreadFactory("switch-serverEventLoopGroup", Thread.MAX_PRIORITY));
  private final EventLoopGroup sendNioEventLoopGroup = new DefaultEventLoopGroup(8,
      new DefaultThreadFactory("switch-sendEventLoopGroup", Thread.MAX_PRIORITY));
  private final EventLoopGroup persistentEventLoopGroup = new DefaultEventLoopGroup(3,
      new DefaultThreadFactory("switch-persistentEventLoopGroup", Thread.MAX_PRIORITY));
  private ChannelFuture connectChannelFuture;

  public ActiveSwitchServerPlus() {
    super();
  }


  @Override
  public void start() {

    bootstrap.group(serverNioEventLoopGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_REUSEADDR, true)
        .remoteAddress(super.sourceAddress)
        .localAddress(localAddress)
        .handler(new ChannelInit());


    try {
      connectChannelFuture = bootstrap.connect().sync();
    } catch (InterruptedException e) {
      logger.error("connect interrupted", e);
    }
  }

  @Override
  public void stop() {
    logger.warn("server shutdown");

    sendNioEventLoopGroup.shutdownGracefully();
    serverNioEventLoopGroup.shutdownGracefully();
  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    monitor.setStatus(connectChannelFuture.channel().isActive());
    return monitor;
  }

  private class ChannelInit extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
      ChannelPipeline ph = socketChannel.pipeline();
      ph.addLast(new StreamInputHandler(prefix));
      ph.addLast(new StreamOutputHandler(prefix));
      ph.addLast(new EmbeddedHandler(new ChannelInitializer<>() {
        @Override
        protected void initChannel(EmbeddedChannel ch) {
          ChannelPipeline ph = ch.pipeline();
          ph.addLast(new PersistentInputHandler(persistentWorker, persistentEventLoopGroup));
          ph.addLast(new PersistentOutputHandler(persistentWorker, persistentEventLoopGroup));
          ph.addLast(new MessageExtractHandler(messageSupplier));
          ph.addLast(new MessageCompressHandler());
          ph.addLast(new ApiDecodeHandler(apiCoder));
          ph.addLast(new ApiEncodeHandler(apiCoder));
          ph.addLast(new BackofficeHandlerByRestPlus(destinationURL, responseApiClz));
        }
      }));
      ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
      ph.addLast(new AdminActiveServerHandler(heartBeat, bootstrap));


    }
  }
}

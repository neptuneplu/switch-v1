package me.card.switchv1.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractActiveSwitchServer extends AbstractSwitchServer
    implements SwitchServer {
  private static final Logger logger = LoggerFactory.getLogger(AbstractActiveSwitchServer.class);

  private final Bootstrap bootstrap = new Bootstrap();

  private final EventLoopGroup serverEventLoopGroup = new NioEventLoopGroup(1,
      new DefaultThreadFactory("switch-serverEventLoopGroup", Thread.MAX_PRIORITY));

  private final EventLoopGroup sendEventLoopGroup = new NioEventLoopGroup(3,
      new DefaultThreadFactory("switch-sendEventLoopGroup", Thread.MAX_PRIORITY));

  private final EventLoopGroup persistentEventLoopGroup = new DefaultEventLoopGroup(2,
      new DefaultThreadFactory("switch-persistentEventLoopGroup", Thread.MAX_PRIORITY));

  private ChannelFuture connectChannelFuture;

  @Override
  public void start() {

    bootstrap.group(serverEventLoopGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_REUSEADDR, true)
        .remoteAddress(sourceAddress)
        .localAddress(localAddress)
        .handler(channelInitializer(persistentEventLoopGroup, sendEventLoopGroup, bootstrap));


    try {
      connectChannelFuture = bootstrap.connect().sync();
    } catch (InterruptedException e) {
      //todo
      logger.warn("connect interrupted", e);
      connectChannelFuture.channel().close();
      stop();
    }
  }

  @Override
  public void stop() {
    logger.warn("server shutdown");
    serverEventLoopGroup.shutdownGracefully();
    persistentEventLoopGroup.shutdownGracefully();
  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    monitor.setStatus(connectChannelFuture.channel().isActive());
    return monitor;
  }

  protected abstract ChannelInitializer<SocketChannel> channelInitializer(
      EventLoopGroup persistentEventLoopGroup,
      EventLoopGroup sendEventLoopGroup,
      Bootstrap bootstrap);
}

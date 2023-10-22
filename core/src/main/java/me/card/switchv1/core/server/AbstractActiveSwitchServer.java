package me.card.switchv1.core.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

  private static final Bootstrap bootstrap = new Bootstrap();

  private static final EventLoopGroup serverGroup = new NioEventLoopGroup(1,
      new DefaultThreadFactory("switch-serverGroup", Thread.MAX_PRIORITY));

  private static final EventLoopGroup sendGroup = new NioEventLoopGroup(3,
      new DefaultThreadFactory("switch-sendGroup", Thread.MAX_PRIORITY));

  private static final EventLoopGroup persistentGroup = new DefaultEventLoopGroup(2,
      new DefaultThreadFactory("switch-persistentGroup", Thread.MAX_PRIORITY));

  private ChannelFuture connectChannelFuture;

  @Override
  public void start() {

    bootstrap.group(serverGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_REUSEADDR, true)
        .remoteAddress(sourceAddress)
        .localAddress(localAddress)
        .handler(getChannelInitializer(persistentGroup, sendGroup, bootstrap));


    connectChannelFuture = bootstrap.connect().addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        logger.info("server connect successful");
      }
    });

  }

  @Override
  public void stop() {
    logger.warn("server shutdown");
    serverGroup.shutdownGracefully();
    persistentGroup.shutdownGracefully();
  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    monitor.setStatus(connectChannelFuture.channel().isActive());
    return monitor;
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      EventLoopGroup persistentGroup, EventLoopGroup sendGroup, Bootstrap bootstrap);
}

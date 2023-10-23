package me.card.switchv1.core.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
    implements SwitchServer, Reconnectable {
  private static final Logger logger = LoggerFactory.getLogger(AbstractActiveSwitchServer.class);

  protected final Bootstrap bootstrap = new Bootstrap();

  private final EventLoopGroup serverGroup =
      new NioEventLoopGroup(1, new DefaultThreadFactory("switch-serverGroup", Thread.MAX_PRIORITY));

  protected final EventLoopGroup sendGroup =
      new NioEventLoopGroup(3, new DefaultThreadFactory("switch-sendGroup", Thread.MAX_PRIORITY));

  protected final EventLoopGroup persistentGroup = new DefaultEventLoopGroup(2,
      new DefaultThreadFactory("switch-persistentGroup", Thread.MAX_PRIORITY));

  private Channel channel;

  @Override
  public void start() {

    bootstrap.group(serverGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_REUSEADDR, true)
        .remoteAddress(sourceAddress)
        .localAddress(localAddress)
        .handler(getChannelInitializer(this));

    connect();
  }

  @Override
  public void connect() {
    bootstrap.connect().addListener(future -> logger.info("connecting"));
  }

  @Override
  public void setChannel(Channel channel) {
    this.channel = channel;
    serverMonitor.setDesc(channel.toString());
    serverMonitor.setStatus(channel.isActive());
  }


  @Override
  public void stop() {
    logger.warn("server shutdown");
    channel.close().syncUninterruptibly();
    sendGroup.shutdownGracefully();
    persistentGroup.shutdownGracefully();
    serverGroup.shutdownGracefully().syncUninterruptibly();

  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    monitor.setStatus(channel.isActive());
    return monitor;
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      Reconnectable reconnectable);
}

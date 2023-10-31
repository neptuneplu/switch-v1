package me.card.switchv1.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPassiveSwitchServer extends AbstractSwitchServer
    implements SwitchServer, Queryable {
  private static final Logger logger = LoggerFactory.getLogger(AbstractPassiveSwitchServer.class);

  private final ServerBootstrap serverBootstrap = new ServerBootstrap();
  private final EventLoopGroup bossGroup;
  private final EventLoopGroup workerGroup;
  protected final EventLoopGroup sendGroup;
  protected final EventLoopGroup persistentGroup;
  private Channel channel;

  protected AbstractPassiveSwitchServer(int sendThreads, int persistentThreads) {
    bossGroup =
        new NioEventLoopGroup(1, new DefaultThreadFactory("switch-bossGroup", Thread.MAX_PRIORITY));

    workerGroup = new NioEventLoopGroup(1,
        new DefaultThreadFactory("switch-workerGroup", Thread.MAX_PRIORITY));

    sendGroup = new NioEventLoopGroup(sendThreads,
        new DefaultThreadFactory("switch-sendGroup", Thread.MAX_PRIORITY));

    persistentGroup = new DefaultEventLoopGroup(persistentThreads,
        new DefaultThreadFactory("switch-persistentGroup", Thread.MAX_PRIORITY));
  }

  @Override
  public void start() {
    serverBootstrap.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 100)
        .childHandler(getChannelInitializer(this));

    serverBootstrap.bind(localAddress).addListener(future -> logger.info("bind successful"));

  }

  @Override
  public void stop() {
    logger.warn("server shutdown");
    channel.close().syncUninterruptibly();
    sendGroup.shutdownGracefully();
    persistentGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }


  @Override
  public void setupChannel(Channel channel) {
    this.channel = channel;
    serverMonitor.setDesc(channel.toString());
    serverMonitor.setStatus(channel.isActive());
  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    monitor.setStatus(channel.isActive());
    return monitor;
  }

  @Override
  public void signOn() {
    throw new ServerException("passive server, do not support send signOn message");
  }

  @Override
  public void signOff() {
    throw new ServerException("passive server, do not support send signOff message");
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(Queryable queryable);
}

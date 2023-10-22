package me.card.switchv1.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPassiveSwitchServer extends AbstractSwitchServer
    implements SwitchServer {
  private static final Logger logger = LoggerFactory.getLogger(AbstractPassiveSwitchServer.class);

  private static final ServerBootstrap serverBootstrap = new ServerBootstrap();

  private static final EventLoopGroup bossGroup =
      new NioEventLoopGroup(1, new DefaultThreadFactory("switch-bossGroup", Thread.MAX_PRIORITY));

  private static final EventLoopGroup workerGroup =
      new NioEventLoopGroup(1, new DefaultThreadFactory("switch-workerGroup", Thread.MAX_PRIORITY));

  private static final EventLoopGroup sendGroup = new DefaultEventLoopGroup(8,
      new DefaultThreadFactory("switch-sendGroup", Thread.MAX_PRIORITY));


  @Override
  public void start() {
    serverBootstrap.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 100)
        //todo
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(getChannelInitializer(sendGroup));

    serverBootstrap.bind(localAddress).addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        logger.info("server bind successful");
      }
    });

  }

  @Override
  public void stop() {
    logger.warn("server shutdown");

    sendGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    //todo
    return monitor;
  }


  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      EventLoopGroup sendGroup);
}

package me.card.switchv1.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import me.card.switchv1.core.handler.AdminPassiveServerHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerNioPlus;
import me.card.switchv1.core.handler.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassiveSwitchServerNioPlus extends AbstractSwitchServer implements SwitchServer {
  private static final Logger logger = LoggerFactory.getLogger(PassiveSwitchServerBio.class);

  private final ServerBootstrap serverBootstrap = new ServerBootstrap();
  private final EventLoopGroup bossGroup =
      new NioEventLoopGroup(1, new DefaultThreadFactory("switch-bossGroup", Thread.MAX_PRIORITY));
  private final EventLoopGroup workerGroup =
      new NioEventLoopGroup(1, new DefaultThreadFactory("switch-workerGroup", Thread.MAX_PRIORITY));
  private final EventLoopGroup sendEventLoopGroup = new NioEventLoopGroup(8,
      new DefaultThreadFactory("switch-sendNioEventLoopGroup", Thread.MAX_PRIORITY));

  public PassiveSwitchServerNioPlus() {
    super();
  }

  @Override
  public void start() {
    serverBootstrap.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 100)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInit());

    try {
      serverBootstrap.bind(localAddress).sync();
    } catch (Exception e) {
      logger.error("server start error", e);
    }
  }

  @Override
  public void stop() {
    logger.warn("server shutdown");

    sendEventLoopGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    //todo
    return monitor;
  }

  private class ChannelInit extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
      ChannelPipeline ph = socketChannel.pipeline();
      ph.addLast(new StreamHandler(prefix));
      ph.addLast(
          new BackOfficeHandlerNioPlus(destinationURL, responseApiClz, sendEventLoopGroup,
              messageSupplier, apiCoder, persistentWorker, null));
      ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
      ph.addLast(new AdminPassiveServerHandler(heartBeat));


    }
  }
}

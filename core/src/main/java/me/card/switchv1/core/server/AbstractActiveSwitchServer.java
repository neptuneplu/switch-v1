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
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import me.card.switchv1.core.handler.PersistentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractActiveSwitchServer extends AbstractSwitchServer
    implements SwitchServer, AutoConnectable {
  private static final Logger logger = LoggerFactory.getLogger(AbstractActiveSwitchServer.class);
  public static final AttributeKey<Boolean> ON_LINE_FLAG = AttributeKey.valueOf("ON_LINE");

  protected final Bootstrap bootstrap = new Bootstrap();
  private final EventLoopGroup serverGroup;
  protected final EventLoopGroup sendGroup;
  protected final EventLoopGroup persistentGroup;
  private Channel channel;

  protected AbstractActiveSwitchServer(int sendThreads, int persistentThreads) {
    serverGroup = new NioEventLoopGroup(1,
        new DefaultThreadFactory("switch-serverGroup", Thread.MAX_PRIORITY));

    sendGroup = new NioEventLoopGroup(sendThreads,
        new DefaultThreadFactory("switch-sendGroup", Thread.MAX_PRIORITY));

    persistentGroup = new DefaultEventLoopGroup(persistentThreads,
        new DefaultThreadFactory("switch-persistentGroup", Thread.MAX_PRIORITY));
  }

  @Override
  public void start() {

    bootstrap.group(serverGroup)
        .channel(NioSocketChannel.class)
        .attr(ON_LINE_FLAG, true)
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
  public void setupChannel(Channel channel) {
    this.channel = channel;
    serverMonitor.setDesc(channel.toString());
    serverMonitor.setStatus(channel.isActive());
  }


  @Override
  public void stop() {
    logger.warn("server shutdown start");

    if (Objects.nonNull(channel) && channel.isActive()) {
      channel.attr(ON_LINE_FLAG).getAndSet(false);
      channel.close().syncUninterruptibly();
    }

    sendGroup.shutdownGracefully();
    persistentGroup.shutdownGracefully();
    serverGroup.shutdownGracefully();

  }

  @Override
  public void signOn() {
    logger.debug("sing on message start");
    // consider to generate an event instead of write a message directly
    channel.pipeline().context(PersistentHandler.NAME).writeAndFlush(signOnMessageSupplier.get());
  }

  @Override
  public void signOff() {
    logger.debug("sing off message start");
    channel.pipeline().context(PersistentHandler.NAME).writeAndFlush(signOffMessageSupplier.get());

  }

  @Override
  public ServerMonitor status() {
    ServerMonitor monitor = serverMonitor.copy();
    monitor.setStatus(channel.isActive());
    return monitor;
  }

  protected abstract ChannelInitializer<SocketChannel> getChannelInitializer(
      AutoConnectable autoConnectable);
}

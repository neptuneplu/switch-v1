package me.card.switchv1.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiDecodeHandler;
import me.card.switchv1.core.handler.ApiEncodeHandler;
import me.card.switchv1.core.handler.BackofficeHandlerByRest;
import me.card.switchv1.core.handler.MessageCompressHandler;
import me.card.switchv1.core.handler.MessageExtractHandler;
import me.card.switchv1.core.handler.PersistentInputHandler;
import me.card.switchv1.core.handler.PersistentOutputHandler;
import me.card.switchv1.core.handler.StreamInputHandler;
import me.card.switchv1.core.handler.StreamOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveSwitchServer extends AbstractSwitchServer implements SwitchServer {
  private static final Logger logger = LoggerFactory.getLogger(ActiveSwitchServer.class);

  private final Bootstrap bootstrap = new Bootstrap();
  private final NioEventLoopGroup serverNioEventLoopGroup = new NioEventLoopGroup(1);
  private final NioEventLoopGroup sendNioEventLoopGroup = new NioEventLoopGroup(8);
  private final NioEventLoopGroup persistentEventLoopGroup = new NioEventLoopGroup(1);
  private ChannelFuture connectChannelFuture;

  public ActiveSwitchServer() {
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
      //todo
      logger.warn("connect interrupted", e);
      connectChannelFuture.channel().close();
      stop();
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
      ph.addLast(new PersistentInputHandler(persistentWorker, persistentEventLoopGroup));
      ph.addLast(new PersistentOutputHandler(persistentWorker, persistentEventLoopGroup));
      ph.addLast(new MessageExtractHandler(messageSupplier));
      ph.addLast(new MessageCompressHandler());
      ph.addLast(new ApiDecodeHandler(apiCoder));
      ph.addLast(new ApiEncodeHandler(apiCoder));
      ph.addLast(
          new BackofficeHandlerByRest(destinationURL, sendNioEventLoopGroup, responseApiClz));
      ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
      ph.addLast(new AdminActiveServerHandler(heartBeat, bootstrap));


    }
  }
}

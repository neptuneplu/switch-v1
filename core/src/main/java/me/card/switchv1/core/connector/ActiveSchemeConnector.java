package me.card.switchv1.core.connector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import me.card.switchv1.core.internal.MessageContext;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.MessageHandler;
import me.card.switchv1.core.handler.ProcessHandler;
import me.card.switchv1.core.handler.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveSchemeConnector extends AbstractSchemeConnector
    implements Connector, AutoConnectable {
  private static final Logger logger = LoggerFactory.getLogger(ActiveSchemeConnector.class);
  public static final AttributeKey<Boolean> ON_LINE_FLAG = AttributeKey.valueOf("ON_LINE");

  protected final Bootstrap bootstrap = new Bootstrap();
  private final EventLoopGroup connectorGroup;
  private Channel channel;

  public ActiveSchemeConnector() {
    connectorGroup = new NioEventLoopGroup(1,
        new DefaultThreadFactory("connectorGroup", Thread.MAX_PRIORITY));
  }

  @Override
  public void start() {

    bootstrap.group(connectorGroup)
        .channel(NioSocketChannel.class)
        .attr(ON_LINE_FLAG, true)
        .option(ChannelOption.SO_REUSEADDR, true)
        .remoteAddress(sourceAddress)
        .localAddress(localAddress)
        .handler(getChannelInitializer(this));

    connect();

  }

  private ChannelInitializer<SocketChannel> getChannelInitializer(AutoConnectable autoConnectable) {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(StreamHandler.NAME, new StreamHandler(prefix))
            .addLast(MessageHandler.NAME, new MessageHandler(messageCoder))
            .addLast(ProcessHandler.NAME, new ProcessHandler(processor))
            .addLast(new IdleStateHandler(readIdleTime, 0, 0))
            .addLast(new AdminActiveServerHandler(heartBeat, autoConnectable));
      }
    };
  }

  @Override
  public void connect() {
    bootstrap.connect().addListener(future -> logger.info("connecting"));
  }

  @Override
  public void setupChannel(Channel channel) {
    this.channel = channel;
    connectorMonitor.setDesc(channel.toString());
    connectorMonitor.setStatus(channel.isActive());
  }


  @Override
  public void stop() {
    logger.warn("connector shutdown start");

    if (Objects.nonNull(channel) && channel.isActive()) {
      channel.attr(ON_LINE_FLAG).getAndSet(false);
      channel.close().syncUninterruptibly();
    }

    connectorGroup.shutdownGracefully();

  }

  @Override
  public void signOn() {
    logger.debug("sing on message start");
    // consider to generate an event instead of write a message directly
    channel.pipeline().context("signon").writeAndFlush(signOnMessageSupplier.get());
  }

  @Override
  public void signOff() {
    logger.debug("sing off message start");
    channel.pipeline().context("signoff").writeAndFlush(signOffMessageSupplier.get());

  }

  @Override
  public ConnectorMonitor status() {
    ConnectorMonitor monitor = connectorMonitor.copy();
    if (channel == null) {
      monitor.setStatus(false);
    } else {
      monitor.setStatus(channel.isActive());
    }
    return monitor;
  }

  @Override
  public MessageContext context() {
    if (channel == null) {
      throw new ConnectorException("channel is null");
    } else {
      return new MessageContext(channel);
    }
  }
}

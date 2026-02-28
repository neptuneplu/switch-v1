package me.card.switchv1.core.connector;

import static me.card.switchv1.core.handler.attributes.ChannelAttributes.ON_LINE_FLAG;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import java.util.function.Consumer;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiHandler;
import me.card.switchv1.core.handler.ExceptionIncomeHandler;
import me.card.switchv1.core.handler.ExceptionOutgoHandler;
import me.card.switchv1.core.handler.MessageHandler;
import me.card.switchv1.core.handler.ProcessHandler;
import me.card.switchv1.core.handler.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveSchemeConnector extends AbstractSchemeConnector
    implements Connector, AutoConnectable {
  private static final Logger logger = LoggerFactory.getLogger(ActiveSchemeConnector.class);
  private static final int CONNECTOR_THREADS_NUMBER = 1;

  protected final Bootstrap bootstrap = new Bootstrap();
  private final EventLoopGroup connectorGroup;
  private Channel channel;

  public ActiveSchemeConnector() {
    connectorGroup = new NioEventLoopGroup(CONNECTOR_THREADS_NUMBER,
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
            .addLast(ApiHandler.NAME, new ApiHandler(apiCoder))
            .addLast(ProcessHandler.NAME, new ProcessHandler(processor))
            .addLast(new IdleStateHandler(readIdleTime, 0, 0))
            .addLast(new AdminActiveServerHandler(heartBeat, autoConnectable))
            .addLast(ExceptionIncomeHandler.NAME, new ExceptionIncomeHandler(processor))
            .addLast(ExceptionOutgoHandler.NAME, new ExceptionOutgoHandler(processor))
        ;
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
      monitor.setPendingOutgos(processor.pendingOutgos());
      monitor.setPendingIncomes(processor.pendingIncomes());
    }
    return monitor;
  }

  @Override
  public void write(Api outgoApi, Consumer<Api> succCallback,
                    Consumer<Api> failCallback) {

    logger.info("[stage connector] sendOutgo start: current thread={}, objective thread={}",
        Thread.currentThread().getName(), channel.eventLoop());

    if (channel == null) {
      throw new ConnectorException("channel is null");
    }
    //
    channel.eventLoop().execute(() -> {
      logger.info("[stage connector] sendOutgo netty write: thread={}",
          Thread.currentThread().getName());

      channel.writeAndFlush(outgoApi)
          .addListener((ChannelFutureListener) future -> {
            logger.info("[stage connector listener] sendOutgo listener: thread={}",
                Thread.currentThread().getName());

            if (future.isSuccess()) {
              logger.debug("send outgo successfully");
              if (succCallback != null) {
                succCallback.accept(outgoApi);
              }
            } else {
              logger.error("send outgo failed", future.cause());
              if (failCallback != null) {
                failCallback.accept(outgoApi);
              }
            }
          });
    });
  }

}

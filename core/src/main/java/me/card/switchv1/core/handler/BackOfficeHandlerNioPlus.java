package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerNioPlus extends SimpleChannelInboundHandler<byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerNioPlus.class);

  private final DestinationURL destinationURL;
  private final Class<? extends Api> responseApiClz;
  private final EventLoopGroup sendEventLoopGroup;
  private final Supplier<Message> messageSupplier;
  private final ApiCoder apiCoder;
  private final PersistentWorker persistentWorker;
  private final EventLoopGroup persistentEventLoopGroup;

  public BackOfficeHandlerNioPlus(DestinationURL destinationURL,
                                  Class<? extends Api> responseApiClz,
                                  EventLoopGroup sendEventLoopGroup,
                                  Supplier<Message> messageSupplier, ApiCoder apiCoder,
                                  PersistentWorker persistentWorker,
                                  EventLoopGroup persistentEventLoopGroup) {
    this.destinationURL = destinationURL;
    this.responseApiClz = responseApiClz;
    this.sendEventLoopGroup = sendEventLoopGroup;
    this.messageSupplier = messageSupplier;
    this.apiCoder = apiCoder;
    this.persistentWorker = persistentWorker;
    this.persistentEventLoopGroup = persistentEventLoopGroup;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
    sendEventLoopGroup.submit(() -> {
      BackOfficeClientNioPlus client = new BackOfficeClientNioPlus(
          destinationURL,
          responseApiClz,
          sendEventLoopGroup,
          messageSupplier,
          apiCoder,
          persistentWorker,
          persistentEventLoopGroup);
      try {
        client.send(ctx, msg);
      } catch (Exception e) {
        logger.error("get http request error", e);
        throw new HandlerException("get http request error");
      }
    });
  }
}

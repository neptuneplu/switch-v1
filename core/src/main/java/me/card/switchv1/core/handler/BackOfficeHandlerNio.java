package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerNio extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerNio.class);

  private final DestinationURL destinationURL;
  private final Class<? extends Api> responseApiClz;
  private final EventLoopGroup sendEventLoopGroup;

  public BackOfficeHandlerNio(DestinationURL destinationURL,
                              Class<? extends Api> responseApiClz,
                              EventLoopGroup sendEventLoopGroup) {
    this.destinationURL = destinationURL;
    this.responseApiClz = responseApiClz;
    this.sendEventLoopGroup = sendEventLoopGroup;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api api) {
    sendEventLoopGroup.submit(() -> {
      BackOfficeClientNio client =
          new BackOfficeClientNio(destinationURL, responseApiClz, sendEventLoopGroup);
      try {
        client.send(ctx, api);
      } catch (Exception e) {
        logger.error("get http request error", e);
        throw new HandlerException("get http request error");
      }
    });

  }

}

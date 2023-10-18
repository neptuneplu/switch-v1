package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.Objects;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class BackofficeHandlerByRest extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackofficeHandlerByRest.class);

  private final DestinationURL destinationURL;
  private final Class<? extends Api> responseApiClz;
  private final EventLoopGroup sendEventLoopGroup;
  private final RestTemplate restTemplate;

  public BackofficeHandlerByRest(DestinationURL destinationURL,
                                 Class<? extends Api> responseApiClz,
                                 EventLoopGroup sendNioEventLoopGroup) {
    this.destinationURL = destinationURL;
    this.responseApiClz = responseApiClz;
    this.sendEventLoopGroup = sendNioEventLoopGroup;
    this.restTemplate = new RestTemplate();

  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Api api) {

    if (Objects.isNull(sendEventLoopGroup)) {
      backofficeProcess(channelHandlerContext, api);
    } else {
      sendEventLoopGroup.submit(() -> backofficeProcess(channelHandlerContext, api));
    }

  }

  protected void backofficeProcess(ChannelHandlerContext channelHandlerContext, Api api) {

    Api responseApi;
    try {
      responseApi = restTemplate.postForObject(destinationURL.getUrlString(), api, responseApiClz);
      if (Objects.isNull(responseApi)) {
        throw new HandlerException("backoffice response api is null");
      }
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("response api: %s", responseApi));
      }
      channelHandlerContext.channel().writeAndFlush(responseApi);
    } catch (Exception e) {
      logger.error("handle backoffice error", e);
      api.toResponse("96");
      channelHandlerContext.channel().writeAndFlush(api);
    }
  }


}

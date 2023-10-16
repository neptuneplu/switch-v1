package me.card.switchv1.core.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class BackofficeHandlerByRestPlus extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackofficeHandlerByRestPlus.class);

  private final DestinationURL destinationURL;
  private final RestTemplate restTemplate;
  private final Class<? extends Api> responseApiClz;

  public BackofficeHandlerByRestPlus(DestinationURL destinationURL,
                                     Class<? extends Api> responseApiClz) {
    this.destinationURL = destinationURL;
    this.restTemplate = new RestTemplate();
    this.responseApiClz = responseApiClz;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Api api) {

    Api responseApi;
    try {
      responseApi = restTemplate.postForObject(destinationURL.getUrlString(), api, responseApiClz);
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("response api: %s", responseApi.toString()));
      }
      channelHandlerContext.channel().writeAndFlush(responseApi);
    } catch (Exception e) {
      logger.error("handle backoffice error", e);
      api.toResponse("96");
      channelHandlerContext.channel().writeAndFlush(api);
    }

  }


}

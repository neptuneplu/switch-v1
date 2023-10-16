package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class BackofficeHandlerByRest extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackofficeHandlerByRest.class);

  private final DestinationURL destinationURL;
  private final EventLoopGroup sendEventLoopGroup;
  private final RestTemplate restTemplate;
  private final Class<? extends Api> responseApiClz;

  public BackofficeHandlerByRest(DestinationURL destinationURL,
                                 NioEventLoopGroup sendNioEventLoopGroup,
                                 Class<? extends Api> responseApiClz) {
    this.destinationURL = destinationURL;
    this.sendEventLoopGroup = sendNioEventLoopGroup;
    this.restTemplate = new RestTemplate();
    this.responseApiClz = responseApiClz;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Api api) {

    sendEventLoopGroup.submit(() -> backofficeProcess(channelHandlerContext, api));
//
//    Future<Api> future = sendEventLoopGroup.submit(
//        () -> restTemplate.postForObject(destinationURL.getUrlString(), api, responseApiClz));
//
//    Api reponseApi;
//    try {
//      reponseApi = future.get();
//      if (logger.isDebugEnabled()) {
//        logger.debug(String.format("response api: %s", reponseApi.toString()));
//      }
//      channelHandlerContext.channel().writeAndFlush(reponseApi);
//    } catch (Exception e) {
//      logger.error("handle backoffice error", e);
//      api.toResponse("96");
//      channelHandlerContext.channel().writeAndFlush(api);
//    }
  }

  protected void backofficeProcess(ChannelHandlerContext channelHandlerContext, Api api) {
    System.out.println("thread id: " + Thread.currentThread().getId());

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

package me.card.switchv1.core.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeHandlerByNetty extends SimpleChannelInboundHandler<Api> {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeHandlerByNetty.class);

  private final DestinationURL destinationURL;
  private final Class<? extends Api> responseApiClz;
  private final EventLoopGroup sendEventLoopGroup;

  public BackOfficeHandlerByNetty(DestinationURL destinationURL,
                                  Class<? extends Api> responseApiClz,
                                  EventLoopGroup sendEventLoopGroup) {
    this.destinationURL = destinationURL;
    this.responseApiClz = responseApiClz;
    this.sendEventLoopGroup = sendEventLoopGroup;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Api msg) {
    sendEventLoopGroup.submit(() -> {
      BackOfficeClientByNetty client = new BackOfficeClientByNetty();
      try {
        client.send(ctx, getRequest(msg));
      } catch (Exception e) {
        logger.error("get http request error", e);
        throw new HandlerException("get http request error");
      }
    });

  }

  private FullHttpRequest getRequest(Api api) {
    ObjectMapper mapper = new ObjectMapper();
    String strApi;
    try {
      strApi = mapper.writeValueAsString(api);
    } catch (JsonProcessingException e) {
      logger.error("jackson parser write error", e);
      throw new HandlerException("jackson parser write error");
    }


    ByteBuf bufRequest = Unpooled.copiedBuffer(strApi, CharsetUtil.UTF_8);

    FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
        HttpVersion.HTTP_1_1,
        HttpMethod.POST,
        destinationURL.getUri().toString(),
        bufRequest
    );

    fullHttpRequest.headers().set(HttpHeaderNames.HOST, destinationURL.getDestinationAddress());
    fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH,
        fullHttpRequest.content().readableBytes());
    fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

    return fullHttpRequest;
  }

  class BackOfficeClientByNetty {

    void send(ChannelHandlerContext ctx, FullHttpRequest request) {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group((EventLoopGroup) ctx.executor())
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
              ChannelPipeline ph = ch.pipeline();
              ph.addLast(new HttpClientCodec());
              ph.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
              ph.addLast(new BackOfficeSendHandler(ctx));
            }
          });


      ChannelFuture future;

      try {
        future = bootstrap.connect(destinationURL.getDestinationAddress()).sync();
      } catch (Exception e) {
        logger.error("connect backoffice error", e);
        throw new HandlerException("connect backoffice error");
      }

      future.channel().writeAndFlush(request);

    }
  }


  class BackOfficeSendHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final ChannelHandlerContext serverCtx;

    public BackOfficeSendHandler(ChannelHandlerContext serverCtx) {
      this.serverCtx = serverCtx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
      logger.debug("backoffice response read start");

      if (response == null) {
        throw new HandlerException("msg error");
      }


      byte[] byteContent = new byte[response.content().readableBytes()];
      response.content().readBytes(byteContent);

      ObjectMapper mapper = new ObjectMapper();
      Api api;
      try {
        api = mapper.readValue(byteContent, responseApiClz);
      } catch (Exception e) {
        logger.error("jackson parser read error", e);
        throw new HandlerException("jackson parser read error");
      }

      serverCtx.writeAndFlush(api);

      //release memory
      response.release();
    }
  }
}

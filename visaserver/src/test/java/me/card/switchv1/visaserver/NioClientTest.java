package me.card.switchv1.visaserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import java.net.InetSocketAddress;
import java.net.URI;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.visaapi.VisaApi;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioClientTest {
  private static final Logger logger = LoggerFactory.getLogger(NioClientTest.class);


  @Test
  public void test01() throws Exception {
    EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
//        ph.addLast(new BackOfficeHttpResponseHandler(VisaApi.class, null)); //in
//        ph.addLast(new HttpClientCodec()); //dup
//        ph.addLast(new HttpObjectAggregator(10 * 1024 * 1024)); //dup
        ph.addLast("decoder", new HttpResponseDecoder());
        ph.addLast("encoder", new HttpRequestEncoder());
        ph.addLast(new BackOfficeHttpRequestHandler(
            new DestinationURL(new InetSocketAddress("127.0.0.1", 8088),
                new URI("/auth/visa")))); //out
      }
    });

    Api requestApi = requestApi();
    logger.warn("requestApi : " + requestApi);

    channel.writeOutbound(requestApi);
    ByteBuf byteBuf = channel.readOutbound();

//    logger.warn("byteBuf : " + byteBuf.);


  }

  private Api requestApi() {
    VisaApi visaApi = new VisaApi();
    visaApi.setMTI("0100");
    visaApi.setF4("001000");

    return visaApi;
  }
}

package me.card.switchv1.visaserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import java.net.InetSocketAddress;
import java.net.URI;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.handler.ApiDecodeHandler;
import me.card.switchv1.core.handler.ApiEncodeHandler;
import me.card.switchv1.core.handler.BackofficeHandlerByRest;
import me.card.switchv1.core.handler.MessageCompressHandler;
import me.card.switchv1.core.handler.MessageExtractHandler;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.message.jpos.VisaApiCoder;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.jpos.iso.ISOUtil;
import org.junit.Test;

public class EmbeddedChannelTest {
  public static final String s =
      "160102004600000000000000000000000000000000000810822000000a00000204000000000000001013101047000350f3f2f8f6f1f0f0f0f0f3f5f0f0f00580000000020081";

  @Test
  public void test01() {
    EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new MessageExtractHandler(VisaMessageByJpos::new))
            .addLast(new MessageCompressHandler())
            .addLast(new ApiDecodeHandler((ApiCoder) new VisaApiCoder()))
            .addLast(new ApiEncodeHandler((ApiCoder) new VisaApiCoder()))
            .addLast(new BackofficeHandlerByRest(
                new DestinationURL(new InetSocketAddress("127.0.0.1", 8088), new URI("/auth/visa")),
                VisaApi.class, null));
      }
    });

    channel.writeInbound(ISOUtil.decodeHexDump(s));
    byte[] bytes = channel.readOutbound();

    System.out.println("bytes : " + ISOUtil.byte2hex(bytes));
//    visaMessageByJpos.print();
  }


}

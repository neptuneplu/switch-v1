package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOfficeClientNioPlus extends BackOfficeAbstractClient{
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeClientNioPlus.class);

  private final Supplier<Message> messageSupplier;
  private final ApiCoder apiCoder;
  private final PersistentWorker persistentWorker;
  private final EventLoopGroup persistentEventLoopGroup;

  public BackOfficeClientNioPlus(DestinationURL destinationURL,
                                 Class<? extends Api> responseApiClz,
                                 EventLoopGroup sendEventLoopGroup,
                                 Supplier<Message> messageSupplier, ApiCoder apiCoder,
                                 PersistentWorker persistentWorker,
                                 EventLoopGroup persistentEventLoopGroup) {
    super(destinationURL, responseApiClz, sendEventLoopGroup);
    this.messageSupplier = messageSupplier;
    this.apiCoder = apiCoder;
    this.persistentWorker = persistentWorker;
    this.persistentEventLoopGroup = persistentEventLoopGroup;
  }

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(ChannelHandlerContext ctx) {
    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast(new HttpClientCodec());
        ph.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
        //in
        ph.addLast(new BackOfficeHttpResponseHandler(responseApiClz));
        ph.addLast(new ApiEncodeHandler(apiCoder));
        ph.addLast(new MessageCompressHandler());
        ph.addLast(new PersistentOutputHandler(persistentWorker, persistentEventLoopGroup));
        ph.addLast(new NioPlusClientToServerHandler(ctx));
        //out
        ph.addLast(new BackOfficeHttpRequestHandler(destinationURL));
        ph.addLast(new ApiDecodeHandler(apiCoder));
        ph.addLast(new MessageExtractHandler(messageSupplier));
        ph.addLast(new PersistentInputHandler(persistentWorker, persistentEventLoopGroup));

      }
    };
  }
}

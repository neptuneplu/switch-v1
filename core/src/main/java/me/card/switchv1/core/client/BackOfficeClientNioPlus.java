package me.card.switchv1.core.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import java.util.function.Supplier;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.handler.ApiCodecHandlerNioPlus;
import me.card.switchv1.core.handler.BackOfficeHttpRequestHandler;
import me.card.switchv1.core.handler.BackOfficeHttpResponseHandler;
import me.card.switchv1.core.handler.MessageHandlerNioPlus;
import me.card.switchv1.core.handler.NioClientActionHandler;
import me.card.switchv1.core.handler.PersistentHandlerNioPlus;

public class BackOfficeClientNioPlus extends BackOfficeAbstractClientNio {
  private final Supplier<Message> messageSupplier;
  private final ApiCoder<Api, Message> apiCoder;
  private final PersistentWorker persistentWorker;
  private final EventLoopGroup persistentGroup;
  private final Id id;

  public BackOfficeClientNioPlus(DestinationURL destinationURL, Class<? extends Api> responseApiClz,
                                 Supplier<Message> messageSupplier,
                                 ApiCoder<Api, Message> apiCoder, PersistentWorker persistentWorker,
                                 EventLoopGroup persistentGroup, Id id) {
    super(destinationURL, responseApiClz);
    this.messageSupplier = messageSupplier;
    this.apiCoder = apiCoder;
    this.persistentWorker = persistentWorker;
    this.persistentGroup = persistentGroup;
    this.id = id;
  }

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(ChannelHandlerContext ctx) {
    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpClientCodec())
            .addLast(new HttpObjectAggregator(10 * 1024 * 1024))
            .addLast(new BackOfficeHttpResponseHandler(responseApiClz))//in
            .addLast(new BackOfficeHttpRequestHandler(destinationURL))//out
            .addLast(new ApiCodecHandlerNioPlus(apiCoder)) //dup
            .addLast(persistentGroup, new PersistentHandlerNioPlus(persistentWorker))//dup
            .addLast(new MessageHandlerNioPlus(new DefaultMessageCoder(messageSupplier, id)))//dup
            .addLast(new NioClientActionHandler(ctx::writeAndFlush));//in
      }
    };
  }
}

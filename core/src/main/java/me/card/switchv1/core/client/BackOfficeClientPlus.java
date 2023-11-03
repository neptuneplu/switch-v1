package me.card.switchv1.core.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.TimeUnit;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.handler.ApiCodecHandlerPlus;
import me.card.switchv1.core.handler.ClientFinishHandlerPlus;
import me.card.switchv1.core.handler.MessageHandlerPlus;
import me.card.switchv1.core.handler.PersistentHandlerPlus;
import me.card.switchv1.core.util.Assert;

public class BackOfficeClientPlus extends BackOfficeAbstractClient<ByteBuf> {
  private ApiCoder<Api, Message> apiCoder;
  private PersistentWorker persistentWorker;
  private EventLoopGroup persistentGroup;
  private MessageCoder messageCoder;
  private ApiCodecHandlerPlus apiCodecHandlerPlus;
  private PersistentHandlerPlus persistentHandlerPlus;
  private MessageHandlerPlus messageHandlerPlus;



  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Promise<ByteBuf> promise) {

    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpClientCodec())
            .addLast(new HttpObjectAggregator(10 * 1024 * 1024))
            .addLast(backOfficeHttpResponseHandler)//in
            .addLast(backOfficeHttpRequestHandler)//out
            .addLast(apiCodecHandlerPlus) //dup
            .addLast(persistentGroup, persistentHandlerPlus)//dup
            .addLast(messageHandlerPlus)//dup
            .addLast(new ClientFinishHandlerPlus(promise))//in
            .addLast(new ReadTimeoutHandler(2, TimeUnit.SECONDS))
        ;
      }
    };
  }

  @Override
  public BackOfficeClientPlus init() {
    initCheck();
    super.init0();
    apiCodecHandlerPlus = new ApiCodecHandlerPlus(apiCoder);
    persistentHandlerPlus = new PersistentHandlerPlus(persistentWorker);
    messageHandlerPlus = new MessageHandlerPlus(messageCoder);
    return this;
  }

  private void initCheck() {
    Assert.notNull(apiCoder, "init error, apiCoder is null");
    Assert.notNull(persistentWorker, "init error, persistentWorker is null");
    Assert.notNull(persistentGroup, "init error, persistentGroup is null");
    Assert.notNull(messageCoder, "init error, messageCoder is null");
  }

  public BackOfficeClientPlus messageCoder(
      MessageCoder messageCoder) {
    this.messageCoder = messageCoder;
    return this;
  }

  public BackOfficeClientPlus apiCoder(
      ApiCoder<Api, Message> apiCoder) {
    this.apiCoder = apiCoder;
    return this;
  }

  public BackOfficeClientPlus persistentWorker(
      PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
    return this;
  }

  public BackOfficeClientPlus persistentGroup(EventLoopGroup persistentGroup) {
    this.persistentGroup = persistentGroup;
    return this;
  }


}

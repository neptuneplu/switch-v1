package me.card.switchv1.core.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.core.handler.ApiCodecHandlerPlus;
import me.card.switchv1.core.handler.BackOfficeHttpRequestHandler;
import me.card.switchv1.core.handler.BackOfficeHttpResponseHandler;
import me.card.switchv1.core.handler.ClientFinishHandlerPlus;
import me.card.switchv1.core.handler.MessageHandlerPlus;
import me.card.switchv1.core.handler.PersistentHandlerPlus;

public class BackOfficeClientPlus extends BackOfficeAbstractClient<byte[]> {
  private ApiCoder<Api, Message> apiCoder;
  private PersistentWorker persistentWorker;
  private EventLoopGroup persistentGroup;
  private MessageCoder messageCoder;


  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Promise<byte[]> promise) {
    check();

    return new ChannelInitializer<>() {
      @Override
      public void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpClientCodec())
            .addLast(new HttpObjectAggregator(10 * 1024 * 1024))
            .addLast(new BackOfficeHttpResponseHandler(responseApiClz))//in
            .addLast(new BackOfficeHttpRequestHandler(destinationURL))//out
            .addLast(new ApiCodecHandlerPlus(apiCoder)) //dup
            .addLast(persistentGroup, new PersistentHandlerPlus(persistentWorker))//dup
            .addLast(new MessageHandlerPlus(messageCoder))//dup
            .addLast(new ClientFinishHandlerPlus(promise));//in
      }
    };
  }

  private void check() {
    if (Objects.isNull(responseApiClz)) {
      throw new ClientException("client, responseApiClz is null");
    }
    if (Objects.isNull(destinationURL)) {
      throw new ClientException("client, destinationURL is null");
    }
    if (Objects.isNull(apiCoder)) {
      throw new ClientException("client, apiCoder is null");
    }
    if (Objects.isNull(persistentWorker)) {
      throw new ClientException("client, persistentWorker is null");
    }
    if (Objects.isNull(messageCoder)) {
      throw new ClientException("client, messageCoder is null");
    }

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

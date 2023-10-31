package me.card.switchv1.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.client.BackOfficeClientPlus;
import me.card.switchv1.core.client.Client;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerPlus;
import me.card.switchv1.core.handler.StreamHandler;

public class ActiveSwitchServerPlus extends AbstractActiveSwitchServer {

  protected ActiveSwitchServerPlus(int sendThreads, int persistentThreads) {
    super(sendThreads, persistentThreads);
  }

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(
      AutoConnectable autoConnectable) {
    MessageCoder messageCoder = new DefaultMessageCoder(messageSupplier, id);

    Client<byte[]> client = new BackOfficeClientPlus()
        .messageCoder(messageCoder)
        .apiCoder(apiCoder)
        .persistentGroup(persistentGroup)
        .persistentWorker(persistentWorker)
        .destinationURL(destinationURL)
        .responseApiClz(responseApiClz)
        .init();


    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(StreamHandler.NAME, new StreamHandler(prefix))
            .addLast(sendGroup, BackOfficeHandlerPlus.NAME, new BackOfficeHandlerPlus(client))
            .addLast(new IdleStateHandler(readIdleTime, 0, 0))
            .addLast(new AdminActiveServerHandler(heartBeat, autoConnectable));
      }
    };
  }
}

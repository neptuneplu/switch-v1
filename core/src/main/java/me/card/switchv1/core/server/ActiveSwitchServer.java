package me.card.switchv1.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.client.BackOfficeClient;
import me.card.switchv1.core.client.Client;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiCodecHandler;
import me.card.switchv1.core.handler.BackOfficeHandler;
import me.card.switchv1.core.handler.MessageHandler;
import me.card.switchv1.core.handler.PersistentHandler;
import me.card.switchv1.core.handler.StreamHandler;


public class ActiveSwitchServer extends AbstractActiveSwitchServer {

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Reconnectable reconnectable) {
    Client<Api> client = new BackOfficeClient()
            .destinationURL(destinationURL)
            .responseApiClz(responseApiClz);

    MessageCoder messageCoder = new DefaultMessageCoder(messageSupplier, id);

    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(StreamHandler.NAME, new StreamHandler(prefix))
            .addLast(MessageHandler.NAME, new MessageHandler(messageCoder))
            .addLast(persistentGroup, PersistentHandler.NAME,
                new PersistentHandler(persistentWorker))
            .addLast(ApiCodecHandler.NAME, new ApiCodecHandler(apiCoder))
            .addLast(sendGroup, BackOfficeHandler.NAME, new BackOfficeHandler(client))
            .addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0))
            .addLast(new AdminActiveServerHandler(heartBeat, reconnectable));
      }
    };

  }

}

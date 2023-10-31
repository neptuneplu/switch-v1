package me.card.switchv1.core.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.client.BackOfficeClientPlus;
import me.card.switchv1.core.client.Client;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.handler.AdminPassiveServerHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerPlus;
import me.card.switchv1.core.handler.StreamHandler;

public class PassiveSwitchServerPlus extends AbstractPassiveSwitchServer {

  protected PassiveSwitchServerPlus(int sendThreads, int persistentThreads) {
    super(sendThreads, persistentThreads);
  }

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Queryable queryable) {
    MessageCoder messageCoder = new DefaultMessageCoder(messageSupplier, id);

    Client<ByteBuf> client = new BackOfficeClientPlus()
        .messageCoder(messageCoder)
        .apiCoder(apiCoder)
        .persistentGroup(persistentGroup)
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
            .addLast(new AdminPassiveServerHandler(heartBeat, queryable));
      }
    };
  }


}

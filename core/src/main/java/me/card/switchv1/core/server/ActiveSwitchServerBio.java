package me.card.switchv1.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.client.BackOfficeClientBio;
import me.card.switchv1.core.component.DefaultMessageCoder;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.ApiCodecHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerBio;
import me.card.switchv1.core.handler.MessageHandler;
import me.card.switchv1.core.handler.PersistentHandler;
import me.card.switchv1.core.handler.StreamHandler;

public class ActiveSwitchServerBio extends AbstractActiveSwitchServer {

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Reconnectable reconnectable) {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(StreamHandler.NAME, new StreamHandler(prefix))
            .addLast(MessageHandler.NAME,
                new MessageHandler(new DefaultMessageCoder(messageSupplier, id)))
            .addLast(persistentGroup,
                PersistentHandler.NAME, new PersistentHandler(persistentWorker))
            .addLast(ApiCodecHandler.NAME, new ApiCodecHandler(apiCoder))
            .addLast(sendGroup, BackOfficeHandlerBio.NAME,
                new BackOfficeHandlerBio(new BackOfficeClientBio(destinationURL, responseApiClz)))
            .addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0))
            .addLast(new AdminActiveServerHandler(heartBeat, reconnectable));
      }
    };
  }


}

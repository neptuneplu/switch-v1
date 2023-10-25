package me.card.switchv1.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.client.BackOfficeClientNioPlus;
import me.card.switchv1.core.handler.AdminActiveServerHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerNioPlus;
import me.card.switchv1.core.handler.StreamHandler;

public class ActiveSwitchServerNioPlus extends AbstractActiveSwitchServer {

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Reconnectable reconnectable) {

    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(StreamHandler.NAME, new StreamHandler(prefix))
            .addLast(sendGroup, BackOfficeHandlerNioPlus.NAME,
                new BackOfficeHandlerNioPlus(new BackOfficeClientNioPlus(
                    destinationURL, responseApiClz, messageSupplier, apiCoder, persistentWorker,
                    persistentGroup, id)))
            .addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0))
            .addLast(new AdminActiveServerHandler(heartBeat, reconnectable));
      }
    };
  }
}

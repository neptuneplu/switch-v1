package me.card.switchv1.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.card.switchv1.core.client.BackOfficeClientNioPlus;
import me.card.switchv1.core.handler.AdminPassiveServerHandler;
import me.card.switchv1.core.handler.BackOfficeHandlerNioPlus;
import me.card.switchv1.core.handler.StreamHandler;

public class PassiveSwitchServerNioPlus extends AbstractPassiveSwitchServer {

  @Override
  protected ChannelInitializer<SocketChannel> getChannelInitializer(Queryable queryable) {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast(StreamHandler.NAME, new StreamHandler(prefix));
        ph.addLast(sendGroup, BackOfficeHandlerNioPlus.NAME,
            new BackOfficeHandlerNioPlus(new BackOfficeClientNioPlus(
                destinationURL, responseApiClz, messageSupplier, apiCoder, persistentWorker,
                persistentGroup, id)));
        ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
        ph.addLast(new AdminPassiveServerHandler(heartBeat, queryable));
      }
    };
  }


}

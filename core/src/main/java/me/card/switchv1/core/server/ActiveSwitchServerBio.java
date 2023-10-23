package me.card.switchv1.core.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
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
        ChannelPipeline ph = ch.pipeline();
        ph.addLast(new StreamHandler(prefix));
        ph.addLast(new MessageHandler(new DefaultMessageCoder(messageSupplier, id)));
        ph.addLast(persistentGroup, new PersistentHandler(persistentWorker));
        ph.addLast(new ApiCodecHandler(apiCoder));
        ph.addLast(sendGroup,
            new BackOfficeHandlerBio(new BackOfficeClientBio(destinationURL, responseApiClz)));
        ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
        ph.addLast(new AdminActiveServerHandler(heartBeat, reconnectable));
      }
    };
  }


}

//package me.card.switchv1.core;
//
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.timeout.IdleStateHandler;
//import me.card.switchv1.core.handler.AdminActiveServerHandler;
//import me.card.switchv1.core.handler.ApiDecodeHandler;
//import me.card.switchv1.core.handler.ApiEncodeHandler;
//import me.card.switchv1.core.handler.BackofficeHandlerByRest;
//import me.card.switchv1.core.handler.MessageCompressHandler;
//import me.card.switchv1.core.handler.MessageExtractHandler;
//import me.card.switchv1.core.handler.PersistentInputHandler;
//import me.card.switchv1.core.handler.PersistentOutputHandler;
//import me.card.switchv1.core.handler.StreamInputHandler;
//import me.card.switchv1.core.handler.StreamOutputHandler;
//
//public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
//  @Override
//  protected void initChannel(SocketChannel ch) throws Exception {
//    ChannelPipeline ph = socketChannel.pipeline();
//    ph.addLast(new StreamInputHandler(prefix));
//    ph.addLast(new StreamOutputHandler(prefix));
//    ph.addLast(new PersistentInputHandler(persistentWorker,persistentEventLoopGroup));
//    ph.addLast(new PersistentOutputHandler(persistentWorker,persistentEventLoopGroup));
//    ph.addLast(new MessageExtractHandler(messageSupplier));
//    ph.addLast(new MessageCompressHandler());
//    ph.addLast(new ApiDecodeHandler(apiCoder));
//    ph.addLast(new ApiEncodeHandler(apiCoder));
//    ph.addLast(
//        new BackofficeHandlerByRest(destinationURL, sendNioEventLoopGroup, responseApiClz));
//    ph.addLast(new IdleStateHandler(Integer.parseInt(readIdleTime), 0, 0));
//    ph.addLast(new AdminActiveServerHandler(heartBeat, bootstrap));
//  }
//}

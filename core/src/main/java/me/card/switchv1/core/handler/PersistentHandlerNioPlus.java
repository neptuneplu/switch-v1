package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import java.util.Objects;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentHandlerNioPlus extends MessageToMessageCodec<byte[], byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(PersistentHandlerNioPlus.class);

  private final PersistentWorker persistentWorker;
  private final EventLoopGroup persistentEventLoopGroup;

  public PersistentHandlerNioPlus(PersistentWorker persistentWorker,
                           EventLoopGroup persistentEventLoopGroup) {
    this.persistentWorker = persistentWorker;
    this.persistentEventLoopGroup = persistentEventLoopGroup;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
    logger.debug("persistent input start");

    if (Objects.isNull(persistentEventLoopGroup)) {
      persistentWorker.saveInput(msg);
    } else {
      persistentEventLoopGroup.submit(() -> persistentWorker.saveInput(msg));
    }
    out.add(msg);

  }

  @Override
  protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
    logger.debug("persistent output start");

    if (Objects.isNull(persistentEventLoopGroup)) {
      persistentWorker.saveOutput(msg);
    } else {
      persistentEventLoopGroup.submit(() -> persistentWorker.saveOutput(msg));
    }

    out.add(msg);
  }
}

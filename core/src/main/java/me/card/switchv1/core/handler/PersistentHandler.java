package me.card.switchv1.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.util.List;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class PersistentHandler extends MessageToMessageCodec<Message, Message> {
  private static final Logger logger = LoggerFactory.getLogger(PersistentHandler.class);
  public static final String NAME = "PersistentHandler";

  private final PersistentWorker persistentWorker;

  public PersistentHandler(PersistentWorker persistentWorker) {
    this.persistentWorker = persistentWorker;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("persistent output start");

    persistentWorker.saveOutput(msg);
    out.add(msg);
    logger.debug("persistent output end");

  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
    logger.debug("persistent input start");

    persistentWorker.saveInput(msg);
    out.add(msg);
  }
}

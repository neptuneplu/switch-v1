package me.card.switchv1.component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.function.Supplier;

public class DefaultMessageCoder implements MessageCoder {

  private final Supplier<Message> messageSupplier;
  private final Id id;

  public DefaultMessageCoder(Supplier<Message> messageSupplier, Id id) {
    this.messageSupplier = messageSupplier;
    this.id = id;
  }

  @Override
  public Message extract(ByteBuf byteBuf) {
    Message message = messageSupplier.get();
    message.extract(byteBuf.array());
    message.setSeqNo(id.nextStrSeqNo());
    message.print();
    return message;
  }

  @Override
  public Message postExtractPcs(Message message) {
    return message;
  }

  @Override
  public ByteBuf compress(Message message) {
    if (message.getSeqNo() == null) {
      message.setSeqNo(id.nextStrSeqNo());
    }
    return Unpooled.wrappedBuffer(message.compress());
  }

  @Override
  public Message preCompressPcs(Message message) {
    message.setSeqNo(id.nextStrSeqNo());
    return message;
  }
}

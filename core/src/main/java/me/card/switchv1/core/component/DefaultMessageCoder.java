package me.card.switchv1.core.component;

import java.util.function.Supplier;

public class DefaultMessageCoder implements MessageCoder {

  private final Supplier<Message> messageSupplier;
  private final Id id;

  public DefaultMessageCoder(Supplier<Message> messageSupplier, Id id) {
    this.messageSupplier = messageSupplier;
    this.id = id;
  }

  @Override
  public Message extract(byte[] bytes) {
    Message message = messageSupplier.get();
    message.extract(bytes);
    message.setSeqNo(id.nextStrSeqNo());
    message.print();
    return message;
  }

  @Override
  public byte[] compress(Message message) {
    return message.compress();
  }
}

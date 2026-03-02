package me.card.switchv1.component;

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
  public Message postExtractPcs(Message message) {
    return message;
  }

  @Override
  public byte[] compress(Message message) {
    if (message.getSeqNo() == null) {
      message.setSeqNo(id.nextStrSeqNo());
    }
    return message.compress();
  }

  @Override
  public Message preCompressPcs(Message message) {
    message.setSeqNo(id.nextStrSeqNo());
    return message;
  }
}

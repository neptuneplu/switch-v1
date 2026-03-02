package me.card.switchv1.component;

public interface MessageCoder {

  Message extract(byte[] bytes);

  Message postExtractPcs(Message message);

  byte[] compress(Message message);

  Message preCompressPcs(Message message);
}

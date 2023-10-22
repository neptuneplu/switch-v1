package me.card.switchv1.core.component;

public interface MessageCoder {

  Message extract(byte[] bytes);

  byte[] compress(Message message);
}

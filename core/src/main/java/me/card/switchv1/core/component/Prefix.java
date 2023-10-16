package me.card.switchv1.core.component;

public interface Prefix {

  int getIntPrefix(byte[] bytePrefix);

  byte[] getBytePrefix(int length);

  int getPrefixLength();
}

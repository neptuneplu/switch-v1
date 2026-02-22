package me.card.switchv1.component;

public interface Prefix {

  int getIntPrefix(byte[] bytePrefix);

  byte[] getBytePrefix(int length);

  int getPrefixLength();
}

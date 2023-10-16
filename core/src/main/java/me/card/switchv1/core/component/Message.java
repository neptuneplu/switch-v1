package me.card.switchv1.core.component;


public interface Message {

  void extract(byte[] b);

  byte[] compress();

  void print();
}

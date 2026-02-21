package me.card.switchv1.core.component;


public interface Message {

  void extract(byte[] b);

  byte[] compress();

  String getDbKey();

  void setSeqNo(String no);

  String getSeqNo();

  boolean isRequest();

  boolean isResponse();

  void print();
}

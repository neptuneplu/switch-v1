package me.card.switchv1.component;


public interface Message {

  void extract(byte[] b);

  byte[] compress();

  String getDbKey();

  void setSeqNo(String no);

  String getSeqNo();

  boolean isRequest();

  boolean isResponse();

  CorrelationId correlationId();

  void print();
}

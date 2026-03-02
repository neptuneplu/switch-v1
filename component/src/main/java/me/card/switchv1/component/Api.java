package me.card.switchv1.component;

public interface Api {

  void toResponse(String code);

  String mti();

  Message message();

  void setMessage(Message message);

  boolean isRequest();

  boolean isResponse();

  CorrelationId correlationId();
}

package me.card.switchv1.component;

public interface Api {

  void toResponse(String code);

  String mti();

  CorrelationId correlationId();
}

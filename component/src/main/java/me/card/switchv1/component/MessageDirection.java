package me.card.switchv1.component;

public enum MessageDirection {
  INCOME("I"),
  OUTGO("O");

  private final String code;

  MessageDirection(String code) {
    this.code = code;
  }
}

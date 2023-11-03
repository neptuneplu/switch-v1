package me.card.switchv1.core.util;

public class Assert {

  private Assert() {}

  public static void isNull(Object o, String msg) {
    if (o != null) {
      throw new IllegalArgumentException(msg);
    }
  }


  public static void notNull(Object o, String msg) {
    if (o == null) {
      throw new IllegalArgumentException(msg);
    }
  }
}

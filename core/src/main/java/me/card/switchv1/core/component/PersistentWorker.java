package me.card.switchv1.core.component;

public interface PersistentWorker {

  void saveInput(byte[] bytes);

  void saveOutput(byte[] bytes);
}

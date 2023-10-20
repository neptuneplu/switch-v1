package me.card.switchv1.core.component;

public interface PersistentWorker {

  void saveInput(Message message);

  void saveOutput(Message message);
}

package me.card.switchv1.component;

public interface PersistentWorker {

  void saveInput(Message message);

  void saveOutput(Message message);
}

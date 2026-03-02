package me.card.switchv1.component;

public interface PersistentWorker {

  void saveIncomeMessage(Message message);

  void saveOutgoMessage(Message message);

  void saveIncomeError(Message message);

  void saveOutgoError(Message message);
}

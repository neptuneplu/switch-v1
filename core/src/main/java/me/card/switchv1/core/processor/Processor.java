package me.card.switchv1.core.processor;

import java.util.concurrent.CompletableFuture;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.Message;
import me.card.switchv1.core.connector.Connector;

public interface Processor {

  void handleIncomeRequest(Api api);

  void handleIncomeResponse(Api api);

  CompletableFuture<Api> handleOutgoRequest(Api api);

  void handleOutgoResponse(Api api);

  void setConnector(Connector connector);

  int pendingOutgos();

  int pendingIncomes();

  void saveIncomeMessage(Message message);

  void saveOutgoMessage(Message message);

  void saveIncomeError(Message message);

  void saveOutgoError(Message message);

}

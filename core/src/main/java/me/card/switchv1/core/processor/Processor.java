package me.card.switchv1.core.processor;

import java.util.concurrent.CompletableFuture;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.Message;
import me.card.switchv1.core.connector.Connector;

public interface Processor {

  void handleIncomeRequestAsync(Message message);

  void handleIncomeResponseAsync(Message message);

  CompletableFuture<Api> handleOutgoRequestAsync(Api api);

  void handleOutgoResponseAsync(Api api);

  void setConnector(Connector connector);

  int pendingOutgos();

}

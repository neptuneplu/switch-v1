package me.card.switchv1.core.processor;

import java.util.concurrent.CompletableFuture;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.internal.MessageContext;

public interface Processor {

  void handleIncomeRequestAsync(MessageContext context);

  void handleIncomeResponseAsync(MessageContext context);

  CompletableFuture<Api> handleOutgoRequestAsync(MessageContext context);

  void handleOutgoResponseAsync(MessageContext context);

}

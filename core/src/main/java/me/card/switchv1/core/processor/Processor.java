package me.card.switchv1.core.processor;

import java.util.concurrent.CompletableFuture;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.MessageContext;

public interface Processor {

  void handleIncomeAsync(MessageContext context);

  CompletableFuture<Api> handleOutgoRequestAsync(MessageContext context);

}

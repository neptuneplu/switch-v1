package me.card.switchv1.core.processor;

import java.util.concurrent.Future;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.MessageContext;

public interface Processor {

  void handleIncomeAsync(MessageContext context);

  Future<? extends Api> handleOutgoRequestAsync(MessageContext context);

  void handleOutgoResponseAsync(MessageContext context);

  void handleOutgoAbnormalResponseAsync(MessageContext context);
}

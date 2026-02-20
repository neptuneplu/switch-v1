package me.card.switchv1.core.client;

import java.util.function.Consumer;
import me.card.switchv1.core.component.MessageContext;

public interface ApiClient {
  void call(MessageContext context,
            Consumer<MessageContext> outgoConsumer,
            Consumer<MessageContext> errorConsumer);

}

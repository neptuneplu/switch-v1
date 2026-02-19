package me.card.switchv1.core.client;

import java.util.function.Consumer;
import me.card.switchv1.core.component.RequestContext;

public interface ApiClient {
  void call(RequestContext context,
            Consumer<RequestContext> responseConsumer,
            Consumer<RequestContext> errorConsumer);

}

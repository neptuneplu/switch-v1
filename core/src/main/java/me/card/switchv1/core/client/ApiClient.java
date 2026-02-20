package me.card.switchv1.core.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.MessageContext;

public interface ApiClient {
  CompletableFuture<Api> call(MessageContext context);

}

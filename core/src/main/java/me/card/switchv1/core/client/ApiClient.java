package me.card.switchv1.core.client;

import java.util.concurrent.CompletableFuture;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.internal.MessageContext;

public interface ApiClient {
  CompletableFuture<Api> call(MessageContext context);

}

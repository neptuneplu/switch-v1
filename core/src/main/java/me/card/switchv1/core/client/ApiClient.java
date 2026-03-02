package me.card.switchv1.core.client;

import java.util.concurrent.CompletableFuture;
import me.card.switchv1.component.Api;
import me.card.switchv1.core.internal.ApiContext;

public interface ApiClient {
  CompletableFuture<Api> call(ApiContext context);

}

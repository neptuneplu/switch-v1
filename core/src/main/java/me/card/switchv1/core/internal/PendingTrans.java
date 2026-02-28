package me.card.switchv1.core.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.CorrelationId;

public class PendingTrans {
  private final ConcurrentHashMap<CorrelationId, ApiContext> pendings
      = new ConcurrentHashMap<>();


  public CompletableFuture<Api> register(CorrelationId correlationId, ApiContext context) {
    CompletableFuture<Api> future = new CompletableFuture<>();
    context.setResultFuture(future);
    pendings.put(correlationId, context);
    return future;
  }


  public void complete(CorrelationId correlationId, Api api) {
    ApiContext context = pendings.remove(correlationId);

    if (context != null) {
      context.getResultFuture().complete(api);
    } else {
      throw new PendingException("outgo not register");
    }
  }


  public ApiContext match(CorrelationId correlationId) {
    return pendings.get(correlationId);
  }

  public int pendingCount() {
    return pendings.size();
  }
}

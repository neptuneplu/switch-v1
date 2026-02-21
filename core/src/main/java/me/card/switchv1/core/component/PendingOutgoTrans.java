package me.card.switchv1.core.component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class PendingOutgoTrans {
  private final ConcurrentHashMap<CorrelationId, CompletableFuture<Api>> pendingTrans
      = new ConcurrentHashMap<>();


  public CompletableFuture<Api> registerOutgo(CorrelationId correlationId) {
    CompletableFuture<Api> future = new CompletableFuture<>();
    pendingTrans.put(correlationId, future);
    return future;
  }


  public void completeOutgo(CorrelationId correlationId, Api api) {
    CompletableFuture<Api> future = pendingTrans.remove(correlationId);
    if (future != null) {
      future.complete(api);
    } else {
      throw new PendingOutgoException("outgo not register");
    }
  }


  public void timeoutOutgo(CorrelationId correlationId) {
    CompletableFuture<Api> future = pendingTrans.remove(correlationId);
    if (future != null) {
      future.completeExceptionally(new TimeoutException("visa timeout"));
    }
  }
}

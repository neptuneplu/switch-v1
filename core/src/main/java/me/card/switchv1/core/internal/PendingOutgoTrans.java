package me.card.switchv1.core.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.CorrelationId;

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


//  public void timeout(CorrelationId correlationId) {
//    CompletableFuture<Api> future = pendingTrans.remove(correlationId);
//    if (future != null) {
//      future.completeExceptionally(new TimeoutException("visa timeout"));
//    } else {
//      throw new PendingOutgoException("outgo not register");
//    }
//  }

  public int pendingOutgoCount() {
    return pendingTrans.size();
  }
}

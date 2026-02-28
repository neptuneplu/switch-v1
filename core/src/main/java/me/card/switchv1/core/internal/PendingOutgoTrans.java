package me.card.switchv1.core.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.CorrelationId;

public class PendingOutgoTrans {
  private final ConcurrentHashMap<CorrelationId, ApiContext> pendingTrans
      = new ConcurrentHashMap<>();


  public CompletableFuture<Api> registerOutgo(CorrelationId correlationId, ApiContext context) {
    CompletableFuture<Api> future = new CompletableFuture<>();
    context.setResultFuture(future);
    pendingTrans.put(correlationId, context);
    return future;
  }


  public void completeOutgo(CorrelationId correlationId, Api api) {
    ApiContext context = pendingTrans.remove(correlationId);

    if (context != null) {
      context.getResultFuture().complete(api);
    } else {
      throw new PendingException("outgo not register");
    }
  }


  public ApiContext matchOutgo(CorrelationId correlationId) {
    return pendingTrans.get(correlationId);
  }

  public int pendingOutgoCount() {
    return pendingTrans.size();
  }
}

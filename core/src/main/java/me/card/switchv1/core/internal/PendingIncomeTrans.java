package me.card.switchv1.core.internal;

import java.util.concurrent.ConcurrentHashMap;
import me.card.switchv1.component.CorrelationId;

public class PendingIncomeTrans {
  private final ConcurrentHashMap<CorrelationId, ApiContext> pendingTrans
      = new ConcurrentHashMap<>();

  public void registerIncome(CorrelationId correlationId, ApiContext context) {
    pendingTrans.put(correlationId, context);
  }


  public void completeIncome(CorrelationId correlationId) {
    ApiContext context = pendingTrans.remove(correlationId);

    if (context != null) {
      return;
    } else {
      throw new PendingException("outgo not register");
    }
  }


  public ApiContext matchIncome(CorrelationId correlationId) {
    return pendingTrans.get(correlationId);
  }

  public int pendingIncomeCount() {
    return pendingTrans.size();
  }
}

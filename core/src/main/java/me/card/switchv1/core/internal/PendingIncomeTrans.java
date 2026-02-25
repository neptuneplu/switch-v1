package me.card.switchv1.core.internal;

import java.util.concurrent.ConcurrentHashMap;
import me.card.switchv1.component.CorrelationId;

public class PendingIncomeTrans {
  private final ConcurrentHashMap<CorrelationId, MessageContext> pendingTrans
      = new ConcurrentHashMap<>();

  public void registerOutgo(CorrelationId correlationId, MessageContext context) {
    pendingTrans.put(correlationId, context);
  }


  public void completeOutgo(CorrelationId correlationId) {
    MessageContext context = pendingTrans.remove(correlationId);

    if (context != null) {
      return;
    } else {
      throw new PendingException("outgo not register");
    }
  }


  public MessageContext matchOutgo(CorrelationId correlationId) {
    return pendingTrans.get(correlationId);
  }
}

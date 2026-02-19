package me.card.switchv1.core.processor;

import java.util.concurrent.Future;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.RequestContext;

public interface Processor {

  void handleInboundAsync(RequestContext context);

  Future<? extends Api> handleOutboundRequestAsync(RequestContext context);

  void handleOutboundResponseAsync(RequestContext context);

  void handleOutboundAbnormalResponseAsync(RequestContext context);
}

package me.card.switchv1.core.processor;

import me.card.switchv1.core.component.RequestContext;

public interface Processor {
  void processRequest(RequestContext context);

  void processSuccessResponse(RequestContext context);

  void processErrorResponse(RequestContext context);
}

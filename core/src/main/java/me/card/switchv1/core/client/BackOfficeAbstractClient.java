package me.card.switchv1.core.client;

import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;

public abstract class BackOfficeAbstractClient {
  protected final DestinationURL destinationURL;
  protected final Class<? extends Api> responseApiClz;

  protected BackOfficeAbstractClient(DestinationURL destinationURL,
                                     Class<? extends Api> responseApiClz) {
    this.destinationURL = destinationURL;
    this.responseApiClz = responseApiClz;
  }

}

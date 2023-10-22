package me.card.switchv1.core.client;

import java.util.Objects;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.DestinationURL;
import me.card.switchv1.core.handler.HandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class BackOfficeClientBio extends BackOfficeAbstractClient {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeClientBio.class);

  private static final RestTemplate restTemplate = new RestTemplate();

  public BackOfficeClientBio(DestinationURL destinationURL, Class<? extends Api> responseApiClz) {
    super(destinationURL, responseApiClz);
  }

  public Api send(Api api) {
    Assert.notNull(api, "api is null");

    Api responseApi;
    try {
      responseApi = restTemplate.postForObject(destinationURL.getUrlString(), api, responseApiClz);
      if (Objects.isNull(responseApi)) {
        throw new HandlerException("backoffice response api is null");
      }
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("response api: %s", responseApi));
      }
      return responseApi;
    } catch (Exception e) {
      logger.error("handle backoffice error", e);
      api.toResponse("96");
      return api;
    }
  }

}

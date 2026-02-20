package me.card.switchv1.backoffice;

import me.card.switchv1.visaapi.VisaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackofficeAcqController {
  private static final Logger logger = LoggerFactory.getLogger(BackofficeAcqController.class);

  @PostMapping("/auth/acq/visa")
  public void visaAuth(@RequestBody VisaApi visaApi) {

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("received visa response content: %s", visaApi.toString()));
    }
  }
}

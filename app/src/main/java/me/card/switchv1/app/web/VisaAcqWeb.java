package me.card.switchv1.app.web;


import java.util.concurrent.CompletableFuture;
import jakarta.annotation.Resource;
import me.card.switchv1.api.visa.VisaApi;
import me.card.switchv1.app.service.SchemeService;
import me.card.switchv1.component.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "acq.api.enabled", havingValue = "visa", matchIfMissing = false)
public class VisaAcqWeb {
  private static final Logger logger = LoggerFactory.getLogger(VisaAcqWeb.class);

  @Resource
  SchemeService schemeService;

  @PostMapping("/acq/send")
  public CompletableFuture<Api> send(@RequestBody VisaApi api) {
    logger.debug("************ new acq tran received, send start ************");

    return schemeService.sendOutgoRequestAsync(api);
  }

}

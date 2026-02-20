package me.card.switchv1.visaserver;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.service.VisaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(
    name = "acq.api.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class AcqWeb {
  private static final Logger logger = LoggerFactory.getLogger(AcqWeb.class);

  @Resource
  VisaService visaService;

  @PostMapping("/acq/send")
  public CompletableFuture<VisaApi> send(@RequestBody VisaApi visaApi) {
    logger.debug("************ new acq tran received, send start ************");

    return visaService.sendOutgoRequestAsync(visaApi);
  }

}

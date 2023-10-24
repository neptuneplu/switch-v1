package me.card.switchv1.backoffice;


import me.card.switchv1.visaapi.VisaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackOfficeVisaController {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeVisaController.class);

  @PostMapping("/auth/visa")
  public VisaApi visaAuth(@RequestBody VisaApi visaRequest) {

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("visa request content: %s", visaRequest.toString()));
    }

    if (visaRequest.getMTI().equals("0100")) {
      visaRequest.setMTI("0110");
      setResponse(visaRequest);
    }
    if (visaRequest.getMTI().equals("0200")) {
      visaRequest.setMTI("0210");
      setResponse(visaRequest);
    }
    if (visaRequest.getMTI().equals("0800")) {
      visaRequest.setMTI("0810");
      visaRequest.setF39("00");
    }

    return visaRequest;
  }


  private void setResponse(VisaApi visaApi) {
    visaApi.setF38("888888");
    visaApi.setF39("55");
  }


}

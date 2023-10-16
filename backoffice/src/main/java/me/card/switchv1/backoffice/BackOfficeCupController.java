package me.card.switchv1.backoffice;

import me.card.switchv1.cupapi.CupApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/auth", method = RequestMethod.POST)
public class BackOfficeCupController {
  private static final Logger logger = LoggerFactory.getLogger(BackOfficeCupController.class);

  @RequestMapping("/cup")
  public CupApi cupAuth(@RequestBody CupApi cupRequest) {


    if (logger.isDebugEnabled()) {
      logger.debug(String.format("cup request content: %s", cupRequest.toString()));
    }

    if (cupRequest.getMTI().equals("0100")) {
      cupRequest.setMTI("0110");
      setSuccessResponse(cupRequest);
    }
    if (cupRequest.getMTI().equals("0200")) {
      cupRequest.setMTI("0210");
      cupRequest.setF22(null);
      cupRequest.setF26(null);
      cupRequest.setF35(null);
      cupRequest.setF36(null);
      cupRequest.setF43(null);
      cupRequest.setF52(null);
      cupRequest.setF53(null);
      cupRequest.setF14("2010");
      setSuccessResponse(cupRequest);
    }
    if (cupRequest.getMTI().equals("0820")) {
      cupRequest.setMTI("0830");
      cupRequest.setF39("00");
    }

    return cupRequest;
  }


  private void setSuccessResponse(CupApi cupApi) {
    cupApi.setF38("888888");
    cupApi.setF39("00");
  }

  private void setFailureResponse(CupApi cupApi) {
    cupApi.setF39("96");
  }

}

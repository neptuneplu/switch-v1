package me.card.switchv1.message.cup.jpos;

import java.util.Objects;
import me.card.switchv1.api.cup.CupApi;
import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.message.cup.CupMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CupApiCoder implements ApiCoder<CupApi, CupMessageByJpos> {
  private static final Logger logger = LoggerFactory.getLogger(CupApiCoder.class);

  @Override
  public CupApi messageToApi(CupMessageByJpos cupMessage) {
    logger.debug("messageToApi start");

    CupApi cupApi = new CupApi();
    cupApi.setMTI(cupMessage.getMti());
    cupApi.setDestinationId(cupMessage.getDestinationId());
    cupApi.setSourceId(cupMessage.getSourceId());
    //todo enhancement
    cupApi.setHeader(cupMessage.getByteHeader());
    //
    cupApi.setMessage(cupMessage);

    for (int i = 2; i <= 128; i++) {
      if (cupMessage.hasField(i)) {
        try {
          // String.class
          if (CupApi.class.getDeclaredField("F" + i).getType() == String.class) {
            CupApi.class.getMethod("setF" + i, String.class)
                .invoke(cupApi, cupMessage.getField(i));
          }
        } catch (Exception e) {
          logger.error("message to api transfer error", e);
          throw new CupMessageException("message to api transfer error, field is " + i);
        }

      }
    }


    decodeSpecialField(cupMessage, cupApi);

    if (logger.isDebugEnabled()) {
      logger.debug(cupApi.toString());
    }

    return cupApi;
  }

  private void decodeSpecialField(CupMessageByJpos cupMessage, CupApi cupApi) {

  }

  @Override
  public CupMessageByJpos apiToMessage(CupApi cupApi) {
    logger.debug("apiToMessage start");

    CupMessageByJpos cupMessage = new CupMessageByJpos();
    cupMessage.setMti(cupApi.getMTI());
    //exchange destination and source
    cupMessage.setDestinationId(cupApi.getSourceId());
    cupMessage.setSourceId(cupMessage.getDestinationId());
    //todo enhancement
    cupMessage.setByteHeader(cupApi.getHeader());
    //
    cupApi.setMessage(cupMessage);


    for (int i = 2; i <= 128; i++) {
      String fld = null;
      try {
        if (CupApi.class.getDeclaredField("F" + i).getType() == String.class) {
          fld = (String) CupApi.class.getMethod("getF" + i).invoke(cupApi);
        }
      } catch (NoSuchFieldException noSuchFieldException) {
        continue;
      } catch (Exception e) {
        logger.error("api to message transfer error", e);
        throw new CupMessageException(
            String.format("api to message transfer error, field is: %d", i));
      }

      if (Objects.nonNull(fld)) {
        cupMessage.setField(i, fld);
      }
    }


    encodeSpecialField(cupMessage, cupApi);

    if (logger.isDebugEnabled()) {
      cupMessage.print();
    }

    return cupMessage;

  }

  private void encodeSpecialField(CupMessageByJpos cupMessage, CupApi cupApi) {

  }


  @Override
  public CupMessageByJpos errorMessage(CupMessageByJpos cupMessageByJpos) {
    return cupMessageByJpos.generateErrorMessage("96");

  }

}

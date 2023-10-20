package me.card.switchv1.visaserver.message.jpos;

import java.util.List;
import java.util.Objects;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.visaapi.F3;
import me.card.switchv1.visaapi.F55;
import me.card.switchv1.visaapi.TagValue;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.message.VisaMessageException;
import org.jpos.emv.BinaryEMVTag;
import org.jpos.emv.EMVStandardTagType;
import org.jpos.emv.EMVTagSequence;
import org.jpos.emv.LiteralEMVTag;
import org.jpos.emv.UnknownTagNumberException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.tlv.TLVDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisaApiCoder implements ApiCoder<VisaApi, VisaMessageByJpos> {
  private static final Logger logger = LoggerFactory.getLogger(VisaApiCoder.class);

  @Override
  public VisaApi messageToApi(VisaMessageByJpos visaMessage) {
    logger.debug("messageToApi start");

    VisaApi visaApi = new VisaApi();
    visaApi.setMTI(visaMessage.getMti());
    visaApi.setSeqNo(visaMessage.getSeqNo());
    visaApi.setDestinationId(visaMessage.getDestinationId());
    visaApi.setSourceId(visaMessage.getSourceId());


    for (int i = 2; i <= 128; i++) {
      if (visaMessage.hasField(i)) {
        try {
          // String.class
          if (VisaApi.class.getDeclaredField("F" + i).getType() == String.class) {
            VisaApi.class.getMethod("setF" + i, String.class)
                .invoke(visaApi, visaMessage.getField(i));
          }
        } catch (Exception e) {
          logger.error("message to api transfer error", e);
          throw new VisaMessageException("message to api transfer error, field is " + i);
        }

      }
    }


    decodeSpecialField(visaMessage, visaApi);

    if (logger.isDebugEnabled()) {
      logger.debug(visaApi.toString());
    }

    return visaApi;
  }

  private void decodeSpecialField(VisaMessageByJpos visaMessage, VisaApi visaApi) {
    f3Decode(visaMessage, visaApi);
    f55Decode(visaMessage, visaApi);
  }

  @Override
  public VisaMessageByJpos apiToMessage(VisaApi visaApi) {
    logger.debug("apiToMessage start");

    VisaMessageByJpos visaMessage = new VisaMessageByJpos();
    visaMessage.setMti(visaApi.getMTI());
    visaMessage.setSeqNo(visaApi.getSeqNo());
    //exchange destination and source
    visaMessage.setDestinationId(visaApi.getSourceId());
    visaMessage.setSourceId(visaMessage.getDestinationId());


    for (int i = 2; i <= 128; i++) {
      String fld = null;
      try {
        if (VisaApi.class.getDeclaredField("F" + i).getType() == String.class) {
          fld = (String) VisaApi.class.getMethod("getF" + i).invoke(visaApi);
        }
      } catch (NoSuchFieldException noSuchFieldException) {
        continue;
      } catch (Exception e) {
        logger.error("api to message transfer error", e);
        throw new VisaMessageException(
            String.format("api to message transfer error, field is: %d", i));
      }

      if (Objects.nonNull(fld)) {
        visaMessage.setField(i, fld);
      }
    }


    encodeSpecialField(visaMessage, visaApi);

    if (logger.isDebugEnabled()) {
      visaMessage.print();
    }

    return visaMessage;

  }

  private void encodeSpecialField(VisaMessageByJpos visaMessage, VisaApi visaApi) {
    f3Encode(visaMessage, visaApi);
    f55Encode(visaMessage, visaApi);
  }


  private void f3Decode(VisaMessageByJpos visaMessage, VisaApi visaApi) {
    if (!visaMessage.hasField(3)) {
      return;
    }
    F3 f3 = new F3();
    ISOMsg isoMsgF3 = visaMessage.getValue(3);
    f3.setProcessingCode(isoMsgF3.getString(0));
    f3.setFromAccountNum(isoMsgF3.getString(1));
    f3.setToAccountNum(isoMsgF3.getString(2));

    visaApi.setF3(f3);
  }

  private void f3Encode(VisaMessageByJpos visaMessage, VisaApi visaApi) {
    if (Objects.isNull(visaApi.getF3())) {
      return;
    }

    try {
      f3EncodePcs(visaMessage, visaApi);
    } catch (ISOException e) {
      logger.error("f3 set to message error", e);
      throw new VisaMessageException("f3 set to message error");
    }
  }

  private void f3EncodePcs(VisaMessageByJpos visaMessage, VisaApi visaApi) throws ISOException {
    ISOMsg f3 = new ISOMsg();
    f3.setFieldNumber(3);
    //process code
    ISOField sf1 = new ISOField();
    sf1.setFieldNumber(0);
    sf1.setValue(visaApi.getF3().getProcessingCode());
    f3.set(sf1);
    //from account number
    ISOField sf2 = new ISOField();
    sf2.setFieldNumber(1);
    sf2.setValue(visaApi.getF3().getFromAccountNum());
    f3.set(sf2);
    //to account number
    ISOField sf3 = new ISOField();
    sf3.setFieldNumber(2);
    sf3.setValue(visaApi.getF3().getToAccountNum());
    f3.set(sf3);

    visaMessage.setField(f3);

  }


  private void f55Decode(VisaMessageByJpos visaMessage, VisaApi visaApi) {
    if (!visaMessage.hasField(55)) {
      return;
    }

    F55 f55 = new F55();
    ISOMsg isoF55 = (ISOMsg) visaMessage.getValue(55).getComponent(1);

    EMVTagSequence emvTagSequence = new EMVTagSequence();
    try {
      emvTagSequence.readFrom(isoF55);
    } catch (ISOException e) {
      throw new VisaMessageException("unpack f55 error");
    }


    emvTagSequence.getOrderedList().forEach(tagValue -> {
      TagValue apiTagValue = new TagValue();
      apiTagValue.setTag(tagValue.getTag());
      try {
        if (tagValue instanceof BinaryEMVTag) {
          apiTagValue.setValue(ISOUtil.byte2hex((byte[]) tagValue.getValue()));
        } else {
          if (tagValue instanceof LiteralEMVTag) {
            apiTagValue.setValue((String) tagValue.getValue());
          }
        }
      } catch (ISOException e) {
        throw new VisaMessageException("tagValue  handle error: " + tagValue);
      }
      f55.add(apiTagValue);
    });

    visaApi.setF55(f55);

  }

  private void f55Encode(VisaMessageByJpos visaMessage, VisaApi visaApi) {
    if (Objects.isNull(visaApi.getF55())) {
      return;
    }

    try {
      f55EncodePcs(visaMessage, visaApi);
    } catch (Exception e) {
      logger.error("f55 set to message error", e);
      throw new VisaMessageException("f55 set to message error");
    }

  }

  private void f55EncodePcs(VisaMessageByJpos visaMessage, VisaApi visaApi) throws ISOException {
    ISOMsg f55 = new ISOMsg();
    f55.setFieldNumber(55);
    visaMessage.setField(f55);

    //
    f55.set(0, "01");

    //
    ISOMsg f55Data = new ISOMsg();
    f55Data.setFieldNumber(1);
    f55.set(f55Data);


    List<TagValue> tagValues = visaApi.getF55().getTagValues();
    EMVTagSequence sequence = new EMVTagSequence();
    for (TagValue tagValue : tagValues) {
      try {
        addTag(tagValue, sequence);
      } catch (UnknownTagNumberException e) {
        logger.error("add tag error", e);
        throw new VisaMessageException("add tag error: " + tagValue.toString());
      }
    }


    sequence.writeTo(f55Data);

  }

  private void addTag(TagValue tagValue, EMVTagSequence tagSequence)
      throws UnknownTagNumberException {
    EMVStandardTagType tagType = EMVStandardTagType.forHexCode(tagValue.getTag());
    if (tagType.getFormat() == TLVDataFormat.PACKED_NUMERIC ||
        tagType.getFormat() == TLVDataFormat.PACKED_NUMERIC_DATE_YYMMDD ||
        tagType.getFormat() == TLVDataFormat.PACKED_NUMERIC_TIME_HHMMSS) {
      tagSequence.add(new LiteralEMVTag(tagType, tagValue.getValue()));
    } else {
      tagSequence.add(new BinaryEMVTag(tagType, ISOUtil.decodeHexDump(tagValue.getValue())));
    }

  }

  @Override
  public VisaMessageByJpos errorMessage(VisaMessageByJpos visaMessageByJpos) {
    return visaMessageByJpos.generateErrorMessage("96");

  }

}

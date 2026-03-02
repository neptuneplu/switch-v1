package me.card.switchv1.message.visa.jpos;

import me.card.switchv1.api.visa.MTI;
import me.card.switchv1.api.visa.VisaCorrelationId;
import me.card.switchv1.component.CorrelationId;
import me.card.switchv1.component.Message;
import me.card.switchv1.message.visa.VisaMessageException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.header.BASE1Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisaMessageByJpos implements Message {
  private static final Logger logger = LoggerFactory.getLogger(VisaMessageByJpos.class);

  private static final ISOPackager isoPackager;

  static {
    try {
      isoPackager = new VisaBase1Packager();
    } catch (ISOException e) {
      throw new VisaMessageException("visa message packager error");
    }
  }

  private final BASE1Header header;
  private final ISOMsg body;
  private String seqNo;

  public VisaMessageByJpos() {
    this.header = new BASE1Header();
    this.body = new ISOMsg();
    this.body.setPackager(isoPackager);
  }

//  public boolean isIncome() {
//
//  }

  public String getMti() {
    try {
      return this.body.getMTI();
    } catch (ISOException e) {
      throw new VisaMessageException("get visa mti exception");
    }
  }

  public void setMti(String s) {
    try {
      this.body.setMTI(s);
    } catch (ISOException e) {
      throw new VisaMessageException("set visa mti exception");
    }
  }


  @Override
  public String getSeqNo() {
    return seqNo;
  }

//  @Override
//  public boolean isRequest() {
//    if (getMti().equals("0100")) {
//      return true;
//    }
//    return false;
//  }
//
//  @Override
//  public boolean isResponse() {
//    if (getMti().equals("0110")) {
//      return true;
//    }
//    return false;
//  }

  @Override
  public CorrelationId correlationId() {
    VisaCorrelationId correlationId = new VisaCorrelationId();
    correlationId.setF2(body.getString(2));
    correlationId.setF11(body.getString(11));
    correlationId.setF32(body.getString(32));
    correlationId.setF37(body.getString(37));
    correlationId.setF41(body.getString(41));
    correlationId.setF42(body.getString(42));

    return correlationId;
  }

  @Override
  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  public String getDestinationId() {
    return this.header.getDestination();
  }

  public void setDestinationId(String destinationId) {
    this.header.setDestination(destinationId);
  }

  public String getSourceId() {
    return this.header.getSource();
  }

  public void setSourceId(String sourceId) {
    this.header.setSource(sourceId);
  }

  public boolean hasField(int fldNo) {
    return this.body.hasField(fldNo);
  }

  public String getField(int fldNo) {
    return this.body.getString(fldNo);
  }

  public void setField(int fldNo, String fld) {
    this.body.set(fldNo, fld);
  }

  public void setField(ISOMsg fld) throws ISOException {
    this.body.set(fld);
  }


  public ISOMsg getValue(int fldNo) {
    return (ISOMsg) this.body.getComponent(fldNo);
  }


  @Override
  public void extract(byte[] byteMsg) {
    extractHeader(byteMsg);
    extractBody(byteMsg);
  }

  private void extractHeader(byte[] byteMsg) {
    byte[] byteHeader = new byte[BASE1Header.LENGTH];
    System.arraycopy(byteMsg, 0, byteHeader, 0, byteHeader.length);
    header.unpack(byteHeader);
  }

  private void extractBody(byte[] byteMsg) {
    byte[] byteBody = new byte[byteMsg.length - BASE1Header.LENGTH];
    System.arraycopy(byteMsg, BASE1Header.LENGTH, byteBody, 0, byteBody.length);
    try {
      body.unpack(byteBody);
    } catch (ISOException e) {
      logger.error("visa message body unpack error ", e);
      body.dump(System.out, "-");
      throw new VisaMessageException("visa message body unpack error");
    }

  }

  @Override
  public byte[] compress() {
    int headLength = BASE1Header.LENGTH;
    byte[] byteBody;
    try {
      byteBody = this.body.pack();
    } catch (ISOException e) {
      logger.error("visa message body pack error", e);
      throw new VisaMessageException("visa message body pack error");
    }

    header.setLen(byteBody.length);
    byte[] byteHeader = this.header.pack();

    byte[] msg = new byte[headLength + byteBody.length];
    System.arraycopy(byteHeader, 0, msg, 0, headLength);
    System.arraycopy(byteBody, 0, msg, headLength, byteBody.length);
    return msg;
  }

  @Override
  public String getDbKey() {
    return body.getString(2);
  }

  @Override
  public void print() {
    if (logger.isDebugEnabled()) {
      body.dump(System.out, "-");
    }
  }

  public VisaMessageByJpos copy() {
    VisaMessageByJpos cloneMessage = new VisaMessageByJpos();
    cloneMessage.extract(this.compress());
    return cloneMessage;
  }

  public VisaMessageByJpos generateErrorMessage(String responseCode) {
    VisaMessageByJpos errorMessage = this.copy();
    errorMessage.setField(39, responseCode);
    errorMessage.setDestinationId(this.getSourceId());
    errorMessage.setSourceId(this.getDestinationId());
    errorMessage.setMti(MTI.mapResponseMTI(this.getMti()));
    return errorMessage;

  }
}

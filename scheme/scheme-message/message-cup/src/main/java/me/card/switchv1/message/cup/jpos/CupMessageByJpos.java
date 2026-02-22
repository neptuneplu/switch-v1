package me.card.switchv1.message.cup.jpos;


import me.card.switchv1.api.cup.MTI;
import me.card.switchv1.component.Message;
import me.card.switchv1.message.cup.CupMessageException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CupMessageByJpos implements Message {
  private static final Logger logger = LoggerFactory.getLogger(CupMessageByJpos.class);

  private static final ISOPackager isoPackager;

  static {
    try {
      isoPackager = new CupPackager();
    } catch (ISOException e) {
      throw new CupMessageException("cup message packager error");
    }
  }

  private String seqNo;
  private final CupHeader header;
  private final ISOMsg body;

  public CupMessageByJpos() {
    this.header = new CupHeader();
    this.body = new ISOMsg();
    this.body.setPackager(isoPackager);
  }


  public String getMti() {
    try {
      return this.body.getMTI();
    } catch (ISOException e) {
      throw new CupMessageException("get cup mti exception");
    }
  }

  public void setMti(String s) {
    try {
      this.body.setMTI(s);
    } catch (ISOException e) {
      throw new CupMessageException("set cup mti exception");
    }
  }

  @Override
  public String getSeqNo() {
    return seqNo;
  }

  @Override
  public boolean isRequest() {
    return false;
  }

  @Override
  public boolean isResponse() {
    return false;
  }

  @Override
  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  public byte[] getDestinationId() {
    return this.header.getDestinationId();
  }

  public void setDestinationId(byte[] destinationId) {
    this.header.setDestinationId(destinationId);
  }

  public byte[] getSourceId() {
    return this.header.getSourceId();
  }

  public void setSourceId(byte[] sourceId) {
    this.header.setSourceId(sourceId);
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
    byte[] byteHeader = new byte[CupHeader.LENGTH];
    System.arraycopy(byteMsg, 0, byteHeader, 0, byteHeader.length);
    header.unpack(byteHeader);
  }

  private void extractBody(byte[] byteMsg) {
    byte[] byteBody = new byte[byteMsg.length - CupHeader.LENGTH];
    System.arraycopy(byteMsg, CupHeader.LENGTH, byteBody, 0, byteBody.length);
    try {

      body.unpack(byteBody);
    } catch (ISOException e) {
      logger.error("cup message body unpack error", e);
      body.dump(System.out, "-");
      throw new CupMessageException("cup message body unpack error");
    }

  }

  @Override
  public byte[] compress() {
    int headLength = CupHeader.LENGTH;
    byte[] byteBody;
    try {
      byteBody = this.body.pack();
    } catch (ISOException e) {
      throw new CupMessageException("cup message body pack error");
    }

    //new header
    header.setLen(byteBody.length + headLength);
    byte[] byteHeader = this.header.pack();
    byte[] msg = new byte[headLength + byteBody.length];

    //copy new header
    System.arraycopy(byteHeader, 0, msg, 0, headLength);

    //copy body
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

  public CupMessageByJpos copy() {
    CupMessageByJpos cloneMessage = new CupMessageByJpos();
    cloneMessage.extract(this.compress());
    return cloneMessage;
  }

  public CupMessageByJpos generateErrorMessage(String responseCode) {
    CupMessageByJpos errorMessage = this.copy();
    errorMessage.setField(39, responseCode);
    errorMessage.setDestinationId(this.getSourceId());
    errorMessage.setSourceId(this.getDestinationId());
    errorMessage.setMti(MTI.mapResponseMTI(this.getMti()));
    return errorMessage;

  }

  public byte[] getByteHeader() {
    return header.getOriginal();
  }

  public void setByteHeader(byte[] bytes) {
    header.unpack(bytes);
  }
}

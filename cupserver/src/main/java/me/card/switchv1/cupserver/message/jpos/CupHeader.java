package me.card.switchv1.cupserver.message.jpos;

import org.apache.commons.lang3.StringUtils;

public class CupHeader {
  public static final int LENGTH = 46;
  public static final int H1_LEN = 1;
  public static final int H2_LEN = 1;
  public static final int H3_LEN = 4;
  public static final int H4_LEN = 11;
  public static final int H5_LEN = 11;
  public static final int H6_LEN = 3;
  public static final int H7_LEN = 1;
  public static final int H8_LEN = 8;
  public static final int H9_LEN = 1;
  public static final int H10_LEN = 5;

  private byte[] value;

  private String headerLength;
  private String version;
  private int messageLength;
  private byte[] destinationId;
  private byte[] sourceId;
  private String h6;
  private String batchNumber;
  private String transactionInformation;
  private String userInformation;
  private String rejectCode;


  public CupHeader() {
    value = new byte[LENGTH];
  }

  public void unpack(byte[] bytes) {
    System.arraycopy(bytes, 0, value, 0, LENGTH);
    //des
    byte[] byteDes = new byte[H4_LEN];
    System.arraycopy(bytes, H1_LEN + H2_LEN + H3_LEN, byteDes, 0, H4_LEN);
    destinationId = byteDes;
    //src
    byte[] byteSrc = new byte[H5_LEN];
    System.arraycopy(bytes, H1_LEN + H2_LEN + H3_LEN + H4_LEN, byteSrc, 0, H5_LEN);
    sourceId = byteSrc;

  }

  public byte[] pack() {
    byte[] bytes = new byte[LENGTH];
    System.arraycopy(value, 0, bytes, 0, LENGTH);

    //header length
    System.arraycopy(new byte[] {0x2E}, 0, bytes, 0, H1_LEN);

    //version
    System.arraycopy(new byte[] {0x02}, 0, bytes, H1_LEN, H2_LEN);

    //message length
    byte[] byteLen = StringUtils.leftPad(String.valueOf(messageLength), H3_LEN, '0').getBytes();
    System.arraycopy(byteLen, 0, bytes, H1_LEN + H2_LEN, H3_LEN);

    //des
    System.arraycopy(sourceId, 0, bytes, H1_LEN + H2_LEN + H3_LEN, H4_LEN);
    //src
    System.arraycopy(destinationId, 0, bytes, H1_LEN + H2_LEN + H3_LEN + H4_LEN, H5_LEN);

    return bytes;
  }

  public byte[] getOriginal() {
    byte[] bytes = new byte[LENGTH];
    System.arraycopy(value, 0, bytes, 0, LENGTH);
    return bytes;
  }

  public void setLen(int len) {
    this.messageLength = len;
  }


  public byte[] getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(byte[] destinationId) {
    this.destinationId = destinationId;
  }

  public byte[] getSourceId() {
    return sourceId;
  }

  public void setSourceId(byte[] sourceId) {
    this.sourceId = sourceId;
  }
}

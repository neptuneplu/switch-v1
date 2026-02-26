package me.card.switchv1.app.db;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalTime;

@TableName(value = "message_log")
public class MessageLogPo {
  private String id;
  private String seqNo;
  private String direction;
  private String pan;
  private String stan;
  private String aiic;
  private String rrn;
  private String terminalId;
  private String merchantId;
  private String hexMessage;
  private LocalDate createDate;
  private LocalTime createTime;

  public String getId() {
    return id;
  }

  public MessageLogPo setId(String id) {
    this.id = id;
    return this;
  }

  public String getSeqNo() {
    return seqNo;
  }

  public MessageLogPo setSeqNo(String seqNo) {
    this.seqNo = seqNo;
    return this;
  }

  public String getDirection() {
    return direction;
  }

  public MessageLogPo setDirection(String direction) {
    this.direction = direction;
    return this;
  }

  public String getPan() {
    return pan;
  }

  public MessageLogPo setPan(String pan) {
    this.pan = pan;
    return this;
  }

  public String getStan() {
    return stan;
  }

  public MessageLogPo setStan(String stan) {
    this.stan = stan;
    return this;
  }

  public String getAiic() {
    return aiic;
  }

  public MessageLogPo setAiic(String aiic) {
    this.aiic = aiic;
    return this;
  }

  public String getRrn() {
    return rrn;
  }

  public MessageLogPo setRrn(String rrn) {
    this.rrn = rrn;
    return this;
  }

  public String getTerminalId() {
    return terminalId;
  }

  public MessageLogPo setTerminalId(String terminalId) {
    this.terminalId = terminalId;
    return this;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public MessageLogPo setMerchantId(String merchantId) {
    this.merchantId = merchantId;
    return this;
  }

  public String getHexMessage() {
    return hexMessage;
  }

  public MessageLogPo setHexMessage(String hexMessage) {
    this.hexMessage = hexMessage;
    return this;
  }

  public MessageLogPo setCreateDate(LocalDate createDate) {
    this.createDate = createDate;
    return this;
  }

  public LocalTime getCreateTime() {
    return createTime;
  }

  public MessageLogPo setCreateTime(LocalTime createTime) {
    this.createTime = createTime;
    return this;
  }
}

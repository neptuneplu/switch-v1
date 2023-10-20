package me.card.switchv1.visaserver.db;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalTime;

@TableName(value = "visa_log")
public class VisaLogPo {
  private String id;
  private String seqNo;
  private String direction;
  private String messageKey;
  private String hexMessage;
  private LocalDate createDate;
  private LocalTime createTime;

  public String getId() {
    return id;
  }

  public VisaLogPo setId(String id) {
    this.id = id;
    return this;
  }

  public String getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  public String getDirection() {
    return direction;
  }

  public VisaLogPo setDirection(String direction) {
    this.direction = direction;
    return this;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public VisaLogPo setMessageKey(String messageKey) {
    this.messageKey = messageKey;
    return this;
  }

  public String getHexMessage() {
    return hexMessage;
  }

  public VisaLogPo setHexMessage(String hexMessage) {
    this.hexMessage = hexMessage;
    return this;
  }

  public LocalDate getCreateDate() {
    return createDate;
  }

  public VisaLogPo setCreateDate(LocalDate createDate) {
    this.createDate = createDate;
    return this;
  }

  public LocalTime getCreateTime() {
    return createTime;
  }

  public VisaLogPo setCreateTime(LocalTime createTime) {
    this.createTime = createTime;
    return this;
  }
}

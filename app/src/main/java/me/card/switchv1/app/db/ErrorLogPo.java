package me.card.switchv1.app.db;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalTime;

@TableName(value = "error_log")
public class ErrorLogPo {

  private String id;
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

  public void setId(String id) {
    this.id = id;
  }


  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }


  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }


  public String getStan() {
    return stan;
  }

  public void setStan(String stan) {
    this.stan = stan;
  }


  public String getAiic() {
    return aiic;
  }

  public void setAiic(String aiic) {
    this.aiic = aiic;
  }


  public String getRrn() {
    return rrn;
  }

  public void setRrn(String rrn) {
    this.rrn = rrn;
  }


  public String getTerminalId() {
    return terminalId;
  }

  public void setTerminalId(String terminalId) {
    this.terminalId = terminalId;
  }


  public String getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(String merchantId) {
    this.merchantId = merchantId;
  }


  public String getHexMessage() {
    return hexMessage;
  }

  public void setHexMessage(String hexMessage) {
    this.hexMessage = hexMessage;
  }

  public LocalDate getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDate createDate) {
    this.createDate = createDate;
  }

  public LocalTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalTime createTime) {
    this.createTime = createTime;
  }
}

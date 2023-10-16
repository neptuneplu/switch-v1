package me.card.switchv1.cupapi;

public class F3 {
  private String processingCode;
  private String fromAccountNum;
  private String toAccountNum;

  public String getProcessingCode() {
    return processingCode;
  }

  public void setProcessingCode(String processingCode) {
    this.processingCode = processingCode;
  }

  public String getFromAccountNum() {
    return fromAccountNum;
  }

  public void setFromAccountNum(String fromAccountNum) {
    this.fromAccountNum = fromAccountNum;
  }

  public String getToAccountNum() {
    return toAccountNum;
  }

  public void setToAccountNum(String toAccountNum) {
    this.toAccountNum = toAccountNum;
  }

  @Override
  public String toString() {
    return "F3{" +
        "processingCode='" + processingCode + '\'' +
        ", fromAccountNum='" + fromAccountNum + '\'' +
        ", toAccountNum='" + toAccountNum + '\'' +
        '}';
  }


}

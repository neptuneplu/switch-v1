package me.card.switchv1.core.connector;

public class ConnectorMonitor {
  private String desc;
  private Boolean status;
  private int pendingOutgos;
  private int pendingIncomes;

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public int getPendingOutgos() {
    return pendingOutgos;
  }

  public void setPendingOutgos(int pendingOutgos) {
    this.pendingOutgos = pendingOutgos;
  }

  public int getPendingIncomes() {
    return pendingIncomes;
  }

  public void setPendingIncomes(int pendingIncomes) {
    this.pendingIncomes = pendingIncomes;
  }


  public ConnectorMonitor copy() {
    ConnectorMonitor connectorMonitor = new ConnectorMonitor();
    connectorMonitor.setDesc(this.desc);
//    connectorMonitor.setStatus(this.status);
//    connectorMonitor.setPendingOutgos(this.pendingOutgos);
//    connectorMonitor.setPendingIncomes(this.pendingIncomes);
    return connectorMonitor;
  }
}

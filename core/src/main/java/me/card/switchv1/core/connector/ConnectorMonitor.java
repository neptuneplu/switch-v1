package me.card.switchv1.core.connector;

public class ConnectorMonitor {
  private String desc;
  private Boolean status;
  private int pendingOutgos;

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

  public ConnectorMonitor setPendingOutgos(int pendingOutgos) {
    this.pendingOutgos = pendingOutgos;
    return this;
  }

  public ConnectorMonitor copy() {
    ConnectorMonitor connectorMonitor = new ConnectorMonitor();
    connectorMonitor.setDesc(this.desc);
    connectorMonitor.setStatus(this.status);
    connectorMonitor.setPendingOutgos(this.pendingOutgos);
    return connectorMonitor;
  }
}

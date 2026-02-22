package me.card.switchv1.core.connector;

public class ConnectorMonitor {
  private String desc;
  private Boolean status;

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

  public ConnectorMonitor copy() {
    ConnectorMonitor connectorMonitor = new ConnectorMonitor();
    connectorMonitor.setDesc(this.desc);
    connectorMonitor.setStatus(this.status);
    return connectorMonitor;
  }
}

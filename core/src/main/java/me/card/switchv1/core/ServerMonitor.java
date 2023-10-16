package me.card.switchv1.core;

public class ServerMonitor {
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

  public ServerMonitor copy() {
    ServerMonitor serverMonitor = new ServerMonitor();
    serverMonitor.setStatus(this.status);
    return serverMonitor;
  }
}

package me.card.switchv1.core.component;

import java.util.Objects;

public abstract class HeartBeat {

  private byte[] heartBeatCode;

  protected HeartBeat() {
  }

  protected HeartBeat(byte[] heartBeatCode) {
    this.heartBeatCode = heartBeatCode;
  }

  public byte[] getHeartBeatCode() {
    return this.heartBeatCode;
  }

  public boolean isRequireHeartBeat() {
    return Objects.nonNull(heartBeatCode);
  }
}

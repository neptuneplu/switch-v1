package me.card.switchv1.cupserver.message;


import me.card.switchv1.core.component.HeartBeat;

public class CupHeartBeat extends HeartBeat {
  private static final byte[] heartBeatMsg = new byte[] {0x30, 0x30, 0x30, 0x30};

  public CupHeartBeat() {
    super(heartBeatMsg);
  }
}

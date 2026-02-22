package me.card.switchv1.message.cup;


import me.card.switchv1.component.HeartBeat;

public class CupHeartBeat extends HeartBeat {
  private static final byte[] heartBeatMsg = new byte[] {0x30, 0x30, 0x30, 0x30};

  public CupHeartBeat() {
    super(heartBeatMsg);
  }
}

package me.card.switchv1.message.visa;


import me.card.switchv1.component.HeartBeat;

public class VisaHeartBeat extends HeartBeat {
  public VisaHeartBeat() {
    super(new byte[] {0x00, 0x00});
  }
}

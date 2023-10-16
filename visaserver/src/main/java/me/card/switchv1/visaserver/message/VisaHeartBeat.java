package me.card.switchv1.visaserver.message;

import me.card.switchv1.core.component.HeartBeat;

public class VisaHeartBeat extends HeartBeat {
  public VisaHeartBeat() {
    super(new byte[] {0x00, 0x00});
  }
}

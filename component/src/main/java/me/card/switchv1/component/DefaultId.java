package me.card.switchv1.component;

import java.util.concurrent.atomic.AtomicInteger;

public class DefaultId implements Id {

  private static final String ID_SUFFIX = "10";
  private static final String SEQ_NO_SUFFIX = "0a";
  private static final int MAX = 9999;
  private final AtomicInteger nextValue = new AtomicInteger(0);

  @Override
  public synchronized String nextStrId() {
    //total length 19
    return ID_SUFFIX + String.format("%13d", timePart()) + String.format("%04d", seqPart());
  }

  @Override
  public synchronized String nextStrSeqNo() {
    //total length 19
    return SEQ_NO_SUFFIX + String.format("%13d", timePart()) + String.format("%04d", seqPart());
  }


  private long timePart() {
    return System.currentTimeMillis();
  }

  private int seqPart() {

    if (nextValue.intValue() < MAX) {
      return nextValue.incrementAndGet();
    }

    resetValue();
    return nextValue.get();
  }

  private void resetValue() {
    nextValue.set(0);
  }
}

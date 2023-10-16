package me.card.switchv1.visaserver.message;

import me.card.switchv1.core.component.PersistentWorker;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisaPersistentWorker implements PersistentWorker {
  private static final Logger logger = LoggerFactory.getLogger(VisaPersistentWorker.class);

  @Override
  public void saveInput(byte[] bytes) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("input: %s", ISOUtil.byte2hex(bytes)));
    }
  }

  @Override
  public void saveOutput(byte[] bytes) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("output: %s", ISOUtil.byte2hex(bytes)));
    }
  }


}

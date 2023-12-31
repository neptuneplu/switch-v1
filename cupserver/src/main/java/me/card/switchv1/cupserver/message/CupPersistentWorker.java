package me.card.switchv1.cupserver.message;

import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CupPersistentWorker implements PersistentWorker {
  private static final Logger logger = LoggerFactory.getLogger(CupPersistentWorker.class);

  @Override
  public void saveInput(Message message) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("input: %s", ISOUtil.byte2hex(message.compress())));
    }
  }

  @Override
  public void saveOutput(Message message) {
    if (logger.isInfoEnabled()) {
      logger.info(String.format("output: %s", ISOUtil.byte2hex(message.compress())));
    }
  }


}
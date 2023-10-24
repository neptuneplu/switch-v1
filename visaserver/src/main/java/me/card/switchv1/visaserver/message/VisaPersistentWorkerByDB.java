package me.card.switchv1.visaserver.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import javax.annotation.Resource;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.visaserver.db.VisaLogDao;
import me.card.switchv1.visaserver.db.VisaLogPo;
import me.card.switchv1.visaserver.db.VisaLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisaPersistentWorkerByDB implements PersistentWorker {
  private static final Logger logger = LoggerFactory.getLogger(VisaPersistentWorkerByDB.class);

  @Resource
  VisaLogService visaLogService;

  @Resource
  private Id id;


  @Override
  public void saveInput(Message message) {
    logger.debug("VisaPersistentWorkerByDB save input start");
    visaLogService.addInput(message);
  }

  @Override
  public void saveOutput(Message message) {
    logger.debug("VisaPersistentWorkerByDB save output start");
    try {
      visaLogService.addOutput(message);
    } catch (Exception e) {
      logger.error("save output error", e);
    }
  }

}

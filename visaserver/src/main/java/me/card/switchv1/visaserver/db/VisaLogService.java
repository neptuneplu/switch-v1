package me.card.switchv1.visaserver.db;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.annotation.Resource;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisaLogService {
  private static final Logger logger = LoggerFactory.getLogger(VisaLogService.class);

  @Resource
  VisaLogDao visaLogDao;

  @Resource
  private Id id;

  public void addInput(Message message) {
    VisaLogPo visaLogPo = getPo(message);
    visaLogPo.setDirection("I");

    try {
      visaLogDao.add(visaLogPo);
    } catch (Exception e) {
      logger.error("insert log error", e);
    }
  }

  public void addOutput(Message message) {
    VisaLogPo visaLogPo = getPo(message);
    visaLogPo.setDirection("O");

    try {
      visaLogDao.add(visaLogPo);
    } catch (Exception e) {
      logger.error("insert log error", e);
    }
  }

  private VisaLogPo getPo(Message message) {
    VisaLogPo visaLogPo = new VisaLogPo();
    visaLogPo.setId(id.nextStrId());
    visaLogPo.setSeqNo(message.getSeqNo());
    visaLogPo.setMessageKey(message.getDbKey());
    visaLogPo.setCreateDate(LocalDate.now());
    visaLogPo.setCreateTime(LocalTime.now());
    visaLogPo.setHexMessage(ISOUtil.byte2hex(message.compress()));
    return visaLogPo;
  }
}

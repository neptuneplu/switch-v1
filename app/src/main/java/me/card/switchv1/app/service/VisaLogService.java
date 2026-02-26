package me.card.switchv1.app.service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import me.card.switchv1.api.visa.VisaCorrelationId;
import me.card.switchv1.app.db.MessageLogPo;
import me.card.switchv1.component.Id;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.PersistentWorker;
import me.card.switchv1.message.visa.jpos.VisaMessageByJpos;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

@Component
public class VisaLogService extends LogService implements PersistentWorker {

  @Resource
  private Id id;

  public MessageLogPo getPo(Message message) {
    VisaCorrelationId correlationId = (VisaCorrelationId) message.correlationId();
    MessageLogPo
        messageLogPo = new MessageLogPo();
    messageLogPo.setId(id.nextStrId());
    messageLogPo.setSeqNo(message.getSeqNo());
    messageLogPo.setPan(correlationId.getF2());
    messageLogPo.setStan(correlationId.getF11());
    messageLogPo.setAiic(correlationId.getF32());
    messageLogPo.setRrn(correlationId.getF37());
    messageLogPo.setTerminalId(correlationId.getF41());
    messageLogPo.setMerchantId(correlationId.getF42());
    messageLogPo.setCreateDate(LocalDate.now());
    messageLogPo.setCreateTime(LocalTime.now());
    messageLogPo.setHexMessage(ISOUtil.byte2hex(message.compress()));
    return messageLogPo;
  }
}

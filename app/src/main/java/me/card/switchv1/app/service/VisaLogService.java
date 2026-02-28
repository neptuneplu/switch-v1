package me.card.switchv1.app.service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import me.card.switchv1.api.visa.VisaCorrelationId;
import me.card.switchv1.app.db.ErrorLogPo;
import me.card.switchv1.app.db.MessageLogPo;
import me.card.switchv1.component.Id;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.PersistentWorker;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

@Component
public class VisaLogService extends LogService implements PersistentWorker {

  @Resource
  private Id id;

  @Override
  public MessageLogPo getMessageLogPo(Message message) {
    VisaCorrelationId correlationId = (VisaCorrelationId) message.correlationId();
    MessageLogPo
        messageLogPo = new MessageLogPo();
    messageLogPo.setId(id.nextStrId());
    messageLogPo.setSeqNo(id.nextStrSeqNo());
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

  @Override
  public ErrorLogPo getErrorLogPo(Message message) {
    VisaCorrelationId correlationId = (VisaCorrelationId) message.correlationId();
    ErrorLogPo
        errorLogPo = new ErrorLogPo();
    errorLogPo.setId(id.nextStrId());
    errorLogPo.setPan(correlationId.getF2());
    errorLogPo.setStan(correlationId.getF11());
    errorLogPo.setAiic(correlationId.getF32());
    errorLogPo.setRrn(correlationId.getF37());
    errorLogPo.setTerminalId(correlationId.getF41());
    errorLogPo.setMerchantId(correlationId.getF42());
    errorLogPo.setCreateDate(LocalDate.now());
    errorLogPo.setCreateTime(LocalTime.now());
    errorLogPo.setHexMessage(ISOUtil.byte2hex(message.compress()));
    return errorLogPo;
  }
}

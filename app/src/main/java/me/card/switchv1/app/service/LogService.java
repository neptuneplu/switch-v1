package me.card.switchv1.app.service;

import io.netty.buffer.ByteBufUtil;
import jakarta.annotation.Resource;
import me.card.switchv1.app.db.ErrorLogDao;
import me.card.switchv1.app.db.ErrorLogPo;
import me.card.switchv1.app.db.MessageLogDao;
import me.card.switchv1.app.db.MessageLogPo;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.ApiCoder;
import me.card.switchv1.component.Message;
import me.card.switchv1.component.MessageCoder;
import me.card.switchv1.component.PersistentWorker;
import me.card.switchv1.message.visa.jpos.VisaMessageByJpos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class LogService implements PersistentWorker {
  private static final Logger logger = LoggerFactory.getLogger(LogService.class);

  @Resource
  MessageLogDao messageLogDao;

  @Resource
  ErrorLogDao errorLogDao;

  @Resource
  private ApiCoder<Api, Message> apiCoder;

  @Resource
  private MessageCoder messageCoder;


  public MessageLogPo queryRawMessage(String seqNo, String direction) {
    return query(seqNo, direction);
  }

  public Api queryApi(String seqNo, String direction) {
    MessageLogPo messageLogPo = queryRawMessage(seqNo, direction);
    VisaMessageByJpos visaMessageByJpos = (VisaMessageByJpos) messageCoder.extract(
        ByteBufUtil.decodeHexDump(messageLogPo.getHexMessage()));

    return apiCoder.messageToApi(visaMessageByJpos);

  }

  @Override
  public void saveIncomeMessage(Message message) {
    MessageLogPo messageLogPo = getMessageLogPo(message);
    messageLogPo.setDirection("I");

    try {
      messageLogDao.add(messageLogPo);
    } catch (Exception e) {
      logger.error("insert income message log error", e);
      throw e;
    }
  }

  @Override
  public void saveOutgoMessage(Message message) {
    MessageLogPo messageLogPo = getMessageLogPo(message);
    messageLogPo.setDirection("O");

    try {
      messageLogDao.add(messageLogPo);
    } catch (Exception e) {
      logger.error("insert outgo message log error", e);
      throw e;
    }
  }

  @Override
  public void saveIncomeError(Message message) {
    ErrorLogPo errorLogPo = getErrorLogPo(message);

    try {
      errorLogDao.add(errorLogPo);
    } catch (Exception e) {
      logger.error("insert income error log error", e);
      throw e;
    }
  }

  @Override
  public void saveOutgoError(Message message) {
    ErrorLogPo errorLogPo = getErrorLogPo(message);

    try {
      errorLogDao.add(errorLogPo);
    } catch (Exception e) {
      logger.error("insert outgo error log error", e);
      throw e;
    }
  }
  public MessageLogPo query(String seqNo, String direction) {
    return messageLogDao.query(seqNo, direction);
  }


  public ErrorLogPo queryErrorLog(String id) {
    return errorLogDao.query(id);
  }

  public abstract MessageLogPo getMessageLogPo(Message message);

  public abstract ErrorLogPo getErrorLogPo(Message message);

}

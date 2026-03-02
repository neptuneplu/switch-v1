package me.card.switchv1.app.service;

import io.netty.buffer.ByteBufUtil;
import jakarta.annotation.Resource;
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

  public void saveInput(Message message) {
    MessageLogPo messageLogPo = getPo(message);
    messageLogPo.setDirection("I");

    try {
      messageLogDao.add(messageLogPo);
    } catch (Exception e) {
      logger.error("insert log error", e);
      throw e;
    }
  }

  public void saveOutput(Message message) {
    MessageLogPo messageLogPo = getPo(message);
    messageLogPo.setDirection("O");

    try {
      messageLogDao.add(messageLogPo);
    } catch (Exception e) {
      logger.error("insert log error", e);
      throw e;
    }
  }

  public MessageLogPo query(String seqNo, String direction) {
    return messageLogDao.query(seqNo, direction);
  }

  public abstract MessageLogPo getPo(Message message);

//  public abstract MessageLogPo getPo(Message message) {
//    MessageLogPo
//        messageLogPo = new MessageLogPo();
//    messageLogPo.setId(id.nextStrId());
//    messageLogPo.setSeqNo(message.getSeqNo());
//    messageLogPo.setMessageKey(message.correlationId().);
//    messageLogPo.setCreateDate(LocalDate.now());
//    messageLogPo.setCreateTime(LocalTime.now());
//    messageLogPo.setHexMessage(ISOUtil.byte2hex(message.compress()));
//    return messageLogPo;
//  }
}

package me.card.switchv1.visaserver.service;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.annotation.Resource;
import me.card.switchv1.core.component.Api;
import me.card.switchv1.core.component.ApiCoder;
import me.card.switchv1.core.component.Id;
import me.card.switchv1.core.component.Message;
import me.card.switchv1.core.component.MessageCoder;
import me.card.switchv1.core.component.PersistentWorker;
import me.card.switchv1.visaapi.VisaApi;
import me.card.switchv1.visaserver.db.VisaLogDao;
import me.card.switchv1.visaserver.db.VisaLogPo;
import me.card.switchv1.visaserver.message.jpos.VisaMessageByJpos;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisaLogService implements PersistentWorker {
  private static final Logger logger = LoggerFactory.getLogger(VisaLogService.class);

  @Resource
  VisaLogDao visaLogDao;

  @Resource
  private ApiCoder<Api, Message> apiCoder;

  @Resource
  private MessageCoder messageCoder;

  @Resource
  private Id id;

  public VisaLogPo queryRawMessage(String seqNo, String direction) {
    return query(seqNo, direction);
  }

  public VisaApi queryApi(String seqNo, String direction) {
    VisaLogPo visaLogPo = queryRawMessage(seqNo, direction);
    VisaMessageByJpos visaMessageByJpos = (VisaMessageByJpos) messageCoder.extract(
        Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(visaLogPo.getHexMessage())));

    return (VisaApi) apiCoder.messageToApi(visaMessageByJpos);

  }

  public void saveInput(Message message) {
    VisaLogPo visaLogPo = getPo(message);
    visaLogPo.setDirection("I");

    try {
      visaLogDao.add(visaLogPo);
    } catch (Exception e) {
      logger.error("insert log error", e);
      throw e;
    }
  }

  public void saveOutput(Message message) {
    VisaLogPo visaLogPo = getPo(message);
    visaLogPo.setDirection("O");

    try {
      visaLogDao.add(visaLogPo);
    } catch (Exception e) {
      logger.error("insert log error", e);
      throw e;
    }
  }

  public VisaLogPo query(String seqNo, String direction) {
    return visaLogDao.query(seqNo, direction);
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

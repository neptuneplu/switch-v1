package me.card.switchv1.visaserver.db;

import javax.annotation.Resource;
import me.card.switchv1.visaserver.db.mapper.VisaLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VisaLogDao {
  private static final Logger logger = LoggerFactory.getLogger(VisaLogDao.class);

  @Resource
  VisaLogMapper mapper;

  public void add(VisaLogPo visaLogPo) {
    int rs = mapper.insert(visaLogPo);
    if (rs != 1) {
      logger.error("insert log error");
      throw new RuntimeException("insert log error: " + rs);
    }
  }

}

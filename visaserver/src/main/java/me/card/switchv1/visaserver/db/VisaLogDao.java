package me.card.switchv1.visaserver.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Optional;
import jakarta.annotation.Resource;
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
      throw new VisaDbException("insert log error: " + rs);
    }
  }

  public VisaLogPo query(String seqNo, String direction) {
    LambdaQueryWrapper<VisaLogPo> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(VisaLogPo::getSeqNo, seqNo);
    queryWrapper.eq(VisaLogPo::getDirection, direction);

    Optional<VisaLogPo> optional = Optional.ofNullable(mapper.selectOne(queryWrapper));
    return optional.orElse(null);
  }
}

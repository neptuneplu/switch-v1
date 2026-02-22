package me.card.switchv1.app.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Optional;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageLogDao {
  private static final Logger logger = LoggerFactory.getLogger(
      me.card.switchv1.app.db.MessageLogDao.class);

  @Resource
  me.card.switchv1.app.db.mapper.MessageLogMapper mapper;

  public void add(MessageLogPo messageLogPo) {
    int rs = mapper.insert(messageLogPo);
    if (rs != 1) {
      logger.error("insert log error");
      throw new DbException("insert log error: " + rs);
    }
  }

  public MessageLogPo query(String seqNo, String direction) {
    LambdaQueryWrapper<MessageLogPo> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(MessageLogPo::getSeqNo, seqNo);
    queryWrapper.eq(MessageLogPo::getDirection, direction);

    Optional<MessageLogPo> optional = Optional.ofNullable(mapper.selectOne(queryWrapper));
    return optional.orElse(null);
  }
}

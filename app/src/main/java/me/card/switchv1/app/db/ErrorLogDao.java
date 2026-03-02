package me.card.switchv1.app.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import java.util.Optional;
import me.card.switchv1.app.db.mapper.ErrorLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogDao {
  private static final Logger logger = LoggerFactory.getLogger(ErrorLogDao.class);

  @Resource
  ErrorLogMapper mapper;

  public void add(ErrorLogPo errorLogPo) {
    int rs = mapper.insert(errorLogPo);
    if (rs != 1) {
      logger.error("insert error log error");
      throw new DbException("insert error log error: " + rs);
    }
  }

  public ErrorLogPo query(String id) {
    LambdaQueryWrapper<ErrorLogPo> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ErrorLogPo::getId, id);

    Optional<ErrorLogPo> optional = Optional.ofNullable(mapper.selectOne(queryWrapper));
    return optional.orElse(null);
  }
}

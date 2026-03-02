package me.card.switchv1.app.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.card.switchv1.app.db.ErrorLogPo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ErrorLogMapper extends BaseMapper<ErrorLogPo> {

}

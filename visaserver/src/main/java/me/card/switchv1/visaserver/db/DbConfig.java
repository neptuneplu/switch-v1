package me.card.switchv1.visaserver.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"me.card.switchv1.visaserver.db.mapper"})
public class DbConfig {
}

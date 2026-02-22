package me.card.switchv1.app.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"me.card.switchv1.app.db.mapper"})
public class DbConfig {
}

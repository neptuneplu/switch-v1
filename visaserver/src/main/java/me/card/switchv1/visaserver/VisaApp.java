package me.card.switchv1.visaserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.card.switchv1"})
@MapperScan({"me.card.switchv1.visaserver.db.mapper"})
public class VisaApp {
  public static void main(String[] args) {
    SpringApplication.run(VisaApp.class, args);
  }
}

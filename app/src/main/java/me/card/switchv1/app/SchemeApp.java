package me.card.switchv1.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.card.switchv1"})
public class SchemeApp {
  public static void main(String[] args) {
    SpringApplication.run(SchemeApp.class, args);
  }
}

package me.card.switchv1.cupserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"me.card.switchv1"})
public class CupApp {
  public static void main(String[] args) {
    SpringApplication.run(CupApp.class, args);
  }
}

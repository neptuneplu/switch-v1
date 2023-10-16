package me.card.switchv1.visaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.card.switchv1"})
public class VisaApp {
  public static void main(String[] args) {
    SpringApplication.run(VisaApp.class, args);
  }
}

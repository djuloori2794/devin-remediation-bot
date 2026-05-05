package com.kd.devinbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DevinBotApplication {

  public static void main(String[] args) {
    SpringApplication.run(DevinBotApplication.class, args);
  }

}

package com.danielbican.ordermanager;

import com.danielbican.ordermanager.mongo.documents.BeforeConvertOrderListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderManagerApplication.class, args);
  }

  @Bean
  public BeforeConvertOrderListener beforeSaveOrderListener() {
    return new BeforeConvertOrderListener();
  }
}

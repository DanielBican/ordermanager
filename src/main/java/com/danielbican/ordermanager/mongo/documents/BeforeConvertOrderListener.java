package com.danielbican.ordermanager.mongo.documents;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.time.LocalDateTime;

public class BeforeConvertOrderListener extends AbstractMongoEventListener<Order> {

  @Override
  public void onBeforeConvert(BeforeConvertEvent<Order> event) {
    Order order = event.getSource();
    if (order.getTotalProductsPrice() == null) {
      order.setTotalProductsPrice();
    }
    if (order.getPlaced() == null) {
      order.setPlaced(LocalDateTime.now());
    }
  }
}

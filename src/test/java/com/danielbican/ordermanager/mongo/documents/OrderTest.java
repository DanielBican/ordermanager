package com.danielbican.ordermanager.mongo.documents;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

class OrderTest {

  @Test
  void setTotalProductsPrice() {

    Order mockOrder =
        new Order(ObjectId.get(), "test@email.com", LocalDateTime.now(), Collections.emptyList());

    mockOrder.setTotalProductsPrice();

    Assertions.assertEquals(BigDecimal.ZERO, mockOrder.getTotalProductsPrice());

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.valueOf(100.00));
    Product mockProduct2 = new Product(ObjectId.get(), "testProduct1", BigDecimal.valueOf(399.99));

    mockOrder = new Order(ObjectId.get(), "test@email.com", LocalDateTime.now(), Arrays.asList(mockProduct1, mockProduct2));

    mockOrder.setTotalProductsPrice();

    Assertions.assertEquals(BigDecimal.valueOf(499.99), mockOrder.getTotalProductsPrice());
  }
}

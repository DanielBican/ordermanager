package com.danielbican.ordermanager.service;

import com.danielbican.ordermanager.mongo.documents.Order;
import com.danielbican.ordermanager.mongo.documents.Product;
import com.danielbican.ordermanager.repository.OrderRepository;
import com.danielbican.ordermanager.repository.ProductRepository;
import com.danielbican.ordermanager.rest.data.OrderRequestBody;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {OrderService.class, OrderRepository.class, ProductRepository.class})
@ActiveProfiles("test")
class OrderServiceTest {

  @Autowired private OrderService orderService;
  @MockBean private OrderRepository orderRepository;
  @MockBean private ProductRepository productRepository;

  @Test
  @DisplayName("Test add order with valid products")
  void addOrderWithValidProducts() {

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.TEN);
    Product mockProduct2 = new Product(ObjectId.get(), "testProduct2", BigDecimal.TEN);

    when(productRepository.findById(mockProduct1.getId())).thenReturn(Optional.of(mockProduct1));
    when(productRepository.findById(mockProduct2.getId())).thenReturn(Optional.of(mockProduct2));

    OrderRequestBody mockOrderRequestBody =
        new OrderRequestBody(
            "test@eamil.com", Arrays.asList(mockProduct1.getId(), mockProduct2.getId()));

    Order mockOrder = new Order(mockOrderRequestBody, Arrays.asList(mockProduct1, mockProduct2));

    when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

    Order savedOrder = orderService.addOrder(mockOrderRequestBody);

    Assertions.assertEquals(mockOrder, savedOrder);
  }

  @Test
  @DisplayName("Test add order with invalid products")
  void addOrderWithInvalidProducts() {

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.TEN);

    when(productRepository.findById(mockProduct1.getId())).thenReturn(Optional.of(mockProduct1));

    Assertions.assertThrows(
        ResponseStatusException.class,
        () -> {
          Product mockProduct2 = new Product(null, "testProduct2", BigDecimal.TEN);

          OrderRequestBody mockOrderRequestBody =
              new OrderRequestBody(
                  "test@eamil.com", Arrays.asList(mockProduct1.getId(), mockProduct2.getId()));

          orderService.addOrder(mockOrderRequestBody);
        });
  }

  @Test
  @DisplayName("Test get orders with start date greater than end date")
  void getOrders() {

    LocalDateTime start = LocalDateTime.now().plusDays(1);
    LocalDateTime end = LocalDateTime.now();

    Assertions.assertThrows(
        ResponseStatusException.class, () -> orderService.getOrders(start, end));
  }
}

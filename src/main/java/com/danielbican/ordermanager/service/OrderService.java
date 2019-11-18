package com.danielbican.ordermanager.service;

import com.danielbican.ordermanager.mongo.documents.Order;
import com.danielbican.ordermanager.mongo.documents.Product;
import com.danielbican.ordermanager.repository.OrderRepository;
import com.danielbican.ordermanager.repository.ProductRepository;
import com.danielbican.ordermanager.rest.data.OrderRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderService {

  private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

  private OrderRepository orderRepository;

  private ProductRepository productRepository;

  public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
  }

  /**
   * Validate OrderRequestBody and create a new Order.
   *
   * @param orderRequestBody The POST to create new order request body.
   * @return The saved Order
   */
  @Transactional
  public Order addOrder(OrderRequestBody orderRequestBody) {

    logger.debug("Processing add new order {}", orderRequestBody);

    List<Product> products =
        orderRequestBody.getProductObjectIds().stream()
            .filter(Objects::nonNull)
            .map(id -> productRepository.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    if (products.size() != orderRequestBody.getProductObjectIds().size()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Please provide a valid id for all products!");
    }

    Order order = new Order(orderRequestBody, products);

    return orderRepository.save(order);
  }

  /**
   * Get all orders between time window. Dates are in ISO format yyyy-MM-dd'T'HH:mm:ss.SSSXXX.
   *
   * @param start Start date
   * @param end End date
   * @return List of orders between start and end dates
   * @throws ResponseStatusException if start date is greater than end date
   */
  public List<Order> getOrders(LocalDateTime start, LocalDateTime end) {

    logger.debug("Processing get all orders between {} and {}", start, end);

    if (start.isAfter(end)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Start date is greater than end date!");
    }

    return orderRepository.findAllByPlacedBetween(start, end);
  }
}

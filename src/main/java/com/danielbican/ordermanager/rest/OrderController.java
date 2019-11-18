package com.danielbican.ordermanager.rest;

import com.danielbican.ordermanager.mongo.documents.Order;
import com.danielbican.ordermanager.rest.data.OrderRequestBody;
import com.danielbican.ordermanager.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class OrderController {

  private OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/api/v1/orders")
  public ResponseEntity<Void> addOrder(
      UriComponentsBuilder uriComponentsBuilder,
      @RequestBody @Valid OrderRequestBody orderRequestBody) {

    Order savedOrder = orderService.addOrder(orderRequestBody);

    URI savedOrderUri =
        uriComponentsBuilder.path("/api/v1/orders/{id}").buildAndExpand(savedOrder.getId()).toUri();

    return ResponseEntity.created(savedOrderUri).build();
  }

  @GetMapping("/api/v1/orders")
  public ResponseEntity<List<Order>> getOrders(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

    List<Order> orders = orderService.getOrders(start, end);

    return ResponseEntity.ok(orders);
  }
}

package com.danielbican.ordermanager.rest;

import com.danielbican.ordermanager.mongo.documents.Order;
import com.danielbican.ordermanager.mongo.documents.Product;
import com.danielbican.ordermanager.repository.OrderRepository;
import com.danielbican.ordermanager.repository.ProductRepository;
import com.danielbican.ordermanager.rest.data.OrderRequestBody;
import com.danielbican.ordermanager.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(
    classes = {
      OrderController.class,
      OrderService.class,
      OrderRepository.class,
      ProductRepository.class
    })
@ActiveProfiles("test")
class OrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private OrderService orderService;

  @MockBean private OrderRepository orderRepository;

  @MockBean private ProductRepository productRepository;

  @Test
  @DisplayName("Test add new order")
  void addOrder() throws Exception {

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.ONE);
    Product mockProduct2 = new Product(ObjectId.get(), "testProduct2", BigDecimal.TEN);

    OrderRequestBody orderRequestBody =
        new OrderRequestBody(
            "test@email.com", Arrays.asList(mockProduct1.getId(), mockProduct2.getId()));

    Order mockOrder =
        new Order(
            ObjectId.get(),
            orderRequestBody.getBuyerEmail(),
            LocalDateTime.now(),
            Arrays.asList(mockProduct1, mockProduct2));

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
    when(productRepository.findById(mockProduct1.getId())).thenReturn(Optional.of(mockProduct1));
    when(productRepository.findById(mockProduct2.getId())).thenReturn(Optional.of(mockProduct2));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/orders")
                .content(mapper.writeValueAsString(orderRequestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(
            redirectedUrl(
                UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("localhost")
                    .path("/api/v1/orders/{id}")
                    .buildAndExpand(mockOrder.getId())
                    .toString()));
  }

  @Test
  @DisplayName("Test get all orders")
  void getOrders() throws Exception {

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.ONE);
    Product mockProduct2 = new Product(ObjectId.get(), "testProduct2", BigDecimal.TEN);

    OrderRequestBody mockOrderRequestBodyBefore =
        new OrderRequestBody("test@email.com", Collections.singletonList(mockProduct1.getId()));

    Order mockOrderBefore =
        new Order(
            ObjectId.get(), mockOrderRequestBodyBefore.getBuyerEmail(),
            LocalDateTime.now().minusDays(1), Collections.singletonList(mockProduct1));

    OrderRequestBody mockOrderRequestBodyBetween =
        new OrderRequestBody(
            "test@email.com", Arrays.asList(mockProduct1.getId(), mockProduct2.getId()));

    Order mockOrderBetween =
        new Order(
            ObjectId.get(),
            mockOrderRequestBodyBetween.getBuyerEmail(),
            LocalDateTime.now(),
            Arrays.asList(mockProduct1, mockProduct2));

    OrderRequestBody mockOrderRequestBodyAfter =
        new OrderRequestBody("test@email.com", Collections.singletonList(mockProduct2.getId()));

    Order mockOrderAfter =
        new Order(
            ObjectId.get(),
            mockOrderRequestBodyAfter.getBuyerEmail(),
            LocalDateTime.now().plusDays(1),
            Collections.singletonList(mockProduct2));

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.registerModule(new JavaTimeModule());

    List<OrderRequestBody> mockOrderRequestBodyList =
        Arrays.asList(
            mockOrderRequestBodyBefore, mockOrderRequestBodyBetween, mockOrderRequestBodyAfter);
    List<Order> mockOrdersList = Arrays.asList(mockOrderBefore, mockOrderBetween, mockOrderAfter);

    when(orderRepository.findAllByPlacedBetween(
            any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(mockOrdersList);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/orders")
                    .param("start", String.valueOf(LocalDateTime.now().minusDays(1)))
                    .param("end", String.valueOf(LocalDateTime.now().plusDays(1)))
                    .content(mapper.writeValueAsString(mockOrderRequestBodyList))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Assertions.assertEquals(
        mapper.writeValueAsString(mockOrdersList), result.getResponse().getContentAsString());
  }
}

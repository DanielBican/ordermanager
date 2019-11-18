package com.danielbican.ordermanager.rest;

import com.danielbican.ordermanager.mongo.documents.Product;
import com.danielbican.ordermanager.repository.ProductRepository;
import com.danielbican.ordermanager.rest.data.ProductRequestBody;
import com.danielbican.ordermanager.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(
    classes = {ProductController.class, ProductService.class, ProductRepository.class})
@ActiveProfiles("test")
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductService productService;

  @MockBean private ProductRepository productRepository;

  @Test
  @DisplayName("Test add new product")
  void addProduct() throws Exception {

    ProductRequestBody mockProductRequestBody1 =
        new ProductRequestBody("testProduct1", BigDecimal.ONE);

    Product mockProduct1 =
        new Product(
            ObjectId.get(), mockProductRequestBody1.getName(), mockProductRequestBody1.getPrice());

    ObjectMapper mapper = new ObjectMapper();

    when(productRepository.save(any(Product.class))).thenReturn(mockProduct1);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/products")
                .content(mapper.writeValueAsString(mockProductRequestBody1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(
            redirectedUrl(
                UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("localhost")
                    .path("/api/v1/products/{id}")
                    .buildAndExpand(mockProduct1.getId())
                    .toString()));
  }

  @Test
  @DisplayName("Test get all products")
  void getProducts() throws Exception {

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.ONE);
    Product mockProduct2 = new Product(ObjectId.get(), "testProduct2", BigDecimal.TEN);

    List<Product> mockProductsList = Arrays.asList(mockProduct1, mockProduct2);

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    when(productRepository.findAll()).thenReturn(mockProductsList);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Assertions.assertEquals(
        mapper.writeValueAsString(mockProductsList), result.getResponse().getContentAsString());
  }

  @Test
  @DisplayName("Test update product not found")
  void updateProductNotFound() throws Exception {

    Product mockProduct1 = new Product(ObjectId.get(), "testProduct1", BigDecimal.ONE);

    ObjectMapper mapper = new ObjectMapper();

    when(productRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/products/{id}", mockProduct1.getId())
                .content(mapper.writeValueAsString(mockProduct1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}

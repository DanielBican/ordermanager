package com.danielbican.ordermanager.mongo.documents;

import com.danielbican.ordermanager.rest.data.OrderRequestBody;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Document(collection = "orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

  @JsonIgnore private ObjectId id;

  @Field(value = "buyer")
  private String buyerEmail;

  private LocalDateTime placed;
  private List<Product> products;

  @Field("total")
  private BigDecimal totalProductsPrice;

  public Order() {}

  /** Used for testing. */
  public Order(ObjectId id, String buyerEmail, LocalDateTime placed, List<Product> products) {
    this.id = id;
    this.buyerEmail = buyerEmail;
    this.placed = placed;
    this.products = products;
    setTotalProductsPrice();
  }

  public Order(OrderRequestBody orderRequestBody, List<Product> products) {
    this.buyerEmail = orderRequestBody.getBuyerEmail();
    this.products = products;
  }

  public void setTotalProductsPrice() {
    this.totalProductsPrice =
        products.stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public ObjectId getId() {
    return id;
  }

  public String getBuyerEmail() {
    return buyerEmail;
  }

  public LocalDateTime getPlaced() {
    return placed;
  }

  public void setPlaced(LocalDateTime placed) {
    this.placed = placed;
  }

  public List<Product> getProducts() {
    return products;
  }

  public BigDecimal getTotalProductsPrice() {
    return totalProductsPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order)) return false;
    Order order = (Order) o;
    return Objects.equals(id, order.id)
        && Objects.equals(buyerEmail, order.buyerEmail)
        && Objects.equals(placed, order.placed)
        && Objects.equals(products, order.products)
        && Objects.equals(totalProductsPrice, order.totalProductsPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, buyerEmail, placed, products, totalProductsPrice);
  }

  @Override
  public String toString() {
    return "Order{"
        + "id="
        + id
        + ", buyerEmail='"
        + buyerEmail
        + '\''
        + ", placed="
        + placed
        + ", products="
        + products
        + ", totalProductsPrice="
        + totalProductsPrice
        + '}';
  }
}

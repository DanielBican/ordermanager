package com.danielbican.ordermanager.rest.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRequestBody {

  @NotNull private String name;
  @NotNull @Positive private BigDecimal price;

  public ProductRequestBody() {}

  /*
   * Used for testing.
   */
  public ProductRequestBody(String name, BigDecimal price) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductRequestBody)) return false;
    ProductRequestBody that = (ProductRequestBody) o;
    return Objects.equals(name, that.name) && Objects.equals(price, that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, price);
  }

  @Override
  public String toString() {
    return "ProductRequestBody{" + "name='" + name + '\'' + ", price=" + price + '}';
  }
}

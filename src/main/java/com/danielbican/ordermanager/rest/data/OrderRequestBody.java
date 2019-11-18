package com.danielbican.ordermanager.rest.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequestBody {

  @NotNull
  @JsonProperty("buyer")
  private String buyerEmail;

  @NotNull
  @NotEmpty
  @JsonProperty("products")
  private List<ObjectId> productObjectIds;

  public OrderRequestBody() {}

  /*
   * Used for testing.
   */
  public OrderRequestBody(String buyerEmail, List<ObjectId> productObjectIds) {
    this.buyerEmail = buyerEmail;
    this.productObjectIds = productObjectIds;
  }

  public String getBuyerEmail() {
    return buyerEmail;
  }

  public void setBuyerEmail(String buyerEmail) {
    this.buyerEmail = buyerEmail;
  }

  public List<ObjectId> getProductObjectIds() {
    return productObjectIds;
  }

  public void setProductObjectIds(List<ObjectId> productObjectIds) {
    this.productObjectIds = productObjectIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderRequestBody)) return false;
    OrderRequestBody that = (OrderRequestBody) o;
    return Objects.equals(buyerEmail, that.buyerEmail)
        && Objects.equals(productObjectIds, that.productObjectIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(buyerEmail, productObjectIds);
  }

  @Override
  public String toString() {
    return "OrderRequestBody{"
        + "buyerEmail='"
        + buyerEmail
        + '\''
        + ", productObjectIds="
        + productObjectIds
        + '}';
  }
}

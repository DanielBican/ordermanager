package com.danielbican.ordermanager.rest;

import com.danielbican.ordermanager.mongo.documents.Product;
import com.danielbican.ordermanager.rest.data.ProductRequestBody;
import com.danielbican.ordermanager.service.ProductService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class ProductController {

  private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * Store a new product. Product fields are validated.
   *
   * @param uriComponentsBuilder Used for extracting base path
   * @param productRequestBody New product to be stored
   * @return HttpStatus.Created with Location header set
   */
  @PostMapping("/api/v1/products")
  public ResponseEntity<Void> addProduct(
      UriComponentsBuilder uriComponentsBuilder,
      @RequestBody @Valid ProductRequestBody productRequestBody) {

    logger.info("Received request to add product {}", productRequestBody);

    Product savedProduct = productService.addProduct(productRequestBody);

    URI savedProductUri =
        uriComponentsBuilder
            .path("/api/v1/products/{id}")
            .buildAndExpand(savedProduct.getId())
            .toUri();

    return ResponseEntity.created(savedProductUri).build();
  }

  /**
   * Get all products stored.
   *
   * @return HttpStatus.OK if all products are retrieved
   */
  @GetMapping("/api/v1/products")
  public ResponseEntity<List<Product>> getProducts() {

    logger.info("Received request to get all products");

    List<Product> products = productService.getProducts();

    return ResponseEntity.ok(products);
  }

  /**
   * Update an already stored product.
   *
   * @param id The ObjectId of the already stored product
   * @param productRequestBody Product details to update
   * @return HttpStatus.OK if the product is updated
   */
  @PatchMapping("/api/v1/products/{id}")
  public ResponseEntity<Void> updateProduct(
      @PathVariable ObjectId id, @RequestBody ProductRequestBody productRequestBody) {

    logger.info("Received request to update product {}", id);

    productService.updateProduct(id, productRequestBody);

    return ResponseEntity.ok().build();
  }
}

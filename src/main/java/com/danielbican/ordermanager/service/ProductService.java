package com.danielbican.ordermanager.service;

import com.danielbican.ordermanager.mongo.documents.Product;
import com.danielbican.ordermanager.repository.ProductRepository;
import com.danielbican.ordermanager.rest.data.ProductRequestBody;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

  private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

  private ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * Add a new product.
   *
   * @param productRequestBody Product details
   * @return Saved product
   */
  public Product addProduct(ProductRequestBody productRequestBody) {

    logger.debug("Processing add new product {}", productRequestBody);

    Product product = new Product(productRequestBody);

    return productRepository.save(product);
  }

  /** @return All products */
  public List<Product> getProducts() {

    logger.debug("Processing get all products");

    return productRepository.findAll();
  }

  /**
   * Update an existing product. Partial update is permitted.
   *
   * @param id Id of the existing product
   * @param productRequestBody Update product details
   * @throws ResponseStatusException if id is not found
   */
  @Transactional
  public void updateProduct(ObjectId id, ProductRequestBody productRequestBody) {

    logger.debug("Processing update existing product {} with values {}", id, productRequestBody);

    Product updatedProduct = productRepository.findById(id).orElse(null);

    if (updatedProduct == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Product with id " + id + " not found!");
    }

    if (productRequestBody.getName() != null) {
      updatedProduct.setName(productRequestBody.getName());
    }

    if (productRequestBody.getPrice() != null) {
      updatedProduct.setPrice(productRequestBody.getPrice());
    }

    productRepository.save(updatedProduct);
  }
}

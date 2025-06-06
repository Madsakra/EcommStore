package com.example.product_store.store.product;

import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

  private final ProductRepository productRepository;

  public ProductValidator(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public void execute(Product product, boolean isUpdate) {

    if (StringUtils.isEmpty(product.getTitle())) {
      throw new ProductNotValidException("Product Title is empty");
    }

    if (product.getStock() <= 0) {
      throw new ProductNotValidException("Product stock should not be 0 or negative");
    }

    if (product.getPrice() <= 0) {
      throw new ProductNotValidException("Product price should not be 0 or negative");
    }

    if (!isUpdate && productRepository.existsByTitleAndPrice(product.getTitle(), product.getPrice())) {
      throw new ProductNotValidException("Duplicate product exists!");
    }
  }
}

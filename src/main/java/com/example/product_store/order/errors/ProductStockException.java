package com.example.product_store.order.errors;

public class ProductStockException extends RuntimeException {
  public ProductStockException(String message) {
    super(message);
  }
}

package com.example.product_store.kafka.exceptions;

public class ProductStockException extends RuntimeException {
  public ProductStockException(String message) {
    super(message);
  }
}

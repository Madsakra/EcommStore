package com.example.product_store.order.exceptions;

public class ProductStockException extends RuntimeException {
  public ProductStockException(String message) {
    super(message);
  }
}

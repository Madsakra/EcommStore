package com.example.product_store.order.exceptions;

public class OrderCreationException extends RuntimeException {
  public OrderCreationException(String message) {
    super(message);
  }
}

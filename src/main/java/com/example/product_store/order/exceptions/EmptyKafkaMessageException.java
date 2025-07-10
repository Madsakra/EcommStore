package com.example.product_store.order.exceptions;

public class EmptyKafkaMessageException extends RuntimeException {
  public EmptyKafkaMessageException(String message) {
    super(message);
  }
}

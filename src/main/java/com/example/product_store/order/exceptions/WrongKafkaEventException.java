package com.example.product_store.order.exceptions;

public class WrongKafkaEventException extends RuntimeException {
  public WrongKafkaEventException(String message) {
    super(message);
  }
}

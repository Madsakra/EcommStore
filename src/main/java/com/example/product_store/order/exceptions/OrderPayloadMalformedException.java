package com.example.product_store.order.exceptions;

public class OrderPayloadMalformedException extends RuntimeException {
  public OrderPayloadMalformedException(String message) {
    super(message);
  }
}

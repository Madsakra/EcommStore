package com.example.product_store.order.exceptions;

import com.example.product_store.order.OrderErrorMessages;

public class WrongKafkaEventException extends RuntimeException {
  public WrongKafkaEventException() {
    super(OrderErrorMessages.WRONG_KAFKA_EVENT.getMessage());
  }
}

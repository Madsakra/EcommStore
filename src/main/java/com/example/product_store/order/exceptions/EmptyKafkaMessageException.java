package com.example.product_store.order.exceptions;

import com.example.product_store.order.OrderErrorMessages;

public class EmptyKafkaMessageException extends RuntimeException {
  public EmptyKafkaMessageException() {
    super(OrderErrorMessages.EMPTY_KAFKA_MESSAGE.getMessage());
  }
}

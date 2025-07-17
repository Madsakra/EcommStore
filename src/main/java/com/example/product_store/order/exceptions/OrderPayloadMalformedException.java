package com.example.product_store.order.exceptions;

import com.example.product_store.order.OrderErrorMessages;

public class OrderPayloadMalformedException extends RuntimeException {
  public OrderPayloadMalformedException() {
    super(OrderErrorMessages.ORDER_PAYLOAD_MALFORMED.getMessage());
  }
}

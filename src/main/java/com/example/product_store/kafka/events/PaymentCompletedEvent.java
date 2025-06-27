package com.example.product_store.kafka.events;

import com.example.product_store.kafka.enums.PaymentStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletedEvent {

  private String orderId;
  private String customerId;
  private BigDecimal balanceReduced;
  private PaymentStatus paymentStatus;
  private String message;

  public PaymentCompletedEvent(
      StartPaymentEvent event, PaymentStatus status, String message) {
    this.orderId = event.getOrderId();
    this.customerId = event.getCustomerId();
    this.balanceReduced = event.getTotalPrice();
    this.paymentStatus = status;
    this.message = message;
  }
}

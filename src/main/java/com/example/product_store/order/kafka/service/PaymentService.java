package com.example.product_store.order.kafka.service;

import com.example.product_store.order.kafka.dto.OrderRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
  private final KafkaTemplate<String, OrderRequest> kafkaTemplate;

  public static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

  public PaymentService(KafkaTemplate<String, OrderRequest> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  // CONSUME PAYMENT EVENT
  @KafkaListener(topics = "payment-commands", groupId = "saga-group")
  public void processPayment(OrderRequest orderRequest) {
    logger.info(
        "Received Order at Payment service:{}. Time of receipt:{}",
        orderRequest.getOrderId(),
        LocalDateTime.now());

    if (orderRequest.getAmount() < 20) {
      logger.info(
          "Not enough cash to pay, payment failed, sending payment failed event to Saga"
              + " orchestra");
      kafkaTemplate.send("payment-events", orderRequest);
    } else {
      logger.info(
          "Payment completed successfully, completed at {}. Sending Payment event back to"
              + " Saga orchestra",
          LocalDateTime.now());
      kafkaTemplate.send("payment-events", orderRequest);
    }
  }
}

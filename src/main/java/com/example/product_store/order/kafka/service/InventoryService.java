package com.example.product_store.order.kafka.service;

import com.example.product_store.order.kafka.dto.OrderRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

  private final KafkaTemplate<String, OrderRequest> kafkaTemplate;
  public static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

  public InventoryService(KafkaTemplate<String, OrderRequest> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = "inventory-commands", groupId = "saga-group")
  public void reserveInventory(OrderRequest orderRequest) {
    logger.info(
        "Received Order at Inventory service:{}. Time of receipt:{}",
        orderRequest.getOrderId(),
        LocalDateTime.now());

    if (orderRequest.getQuantity() > 100) {
      logger.info(
          "Product Does not have enough quantity sending inventoryFailedEvent to saga"
              + " orchestra");
      kafkaTemplate.send("inventory-events", orderRequest);
    } else {
      logger.info(
          "Inventory successfully deducted, completed at {}. Sending Inventory event back to"
              + " Saga orchestra",
          LocalDateTime.now());
      kafkaTemplate.send("inventory-events", orderRequest);
    }
  }
}

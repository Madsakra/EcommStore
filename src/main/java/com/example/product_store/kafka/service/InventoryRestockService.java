package com.example.product_store.kafka.service;

import com.example.product_store.kafka.enums.InventoryStatus;
import com.example.product_store.kafka.events.InventoryCompletedEvent;
import com.example.product_store.kafka.events.StartInventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InventoryRestockService {
  private final KafkaTemplate<String, InventoryCompletedEvent> kafkaTemplate;

  public static final Logger logger =
      LoggerFactory.getLogger(InventoryRestockService.class);

  private final RollBackInventory rollBackInventory;

  public InventoryRestockService(
      KafkaTemplate<String, InventoryCompletedEvent> kafkaTemplate,
      RollBackInventory rollBackInventory) {
    this.kafkaTemplate = kafkaTemplate;
    this.rollBackInventory = rollBackInventory;
  }

  @KafkaListener(topics = "inventory-commands", groupId = "saga-group")
  public void execute(InventoryCompletedEvent inventoryCompletedEvent) {
    logger.info(
            "Received Order at RollBackService. Time of receipt:{}",
            LocalDateTime.now());

    try {
      boolean result = rollBackInventory.execute(inventoryCompletedEvent);
      if (result) {
        String message = "Successfully restocked inventory";
        logger.info(message);
        // SEND KAFKA EVENT BACK TO ORDER SERVICE

      }
    } catch (Exception ex) {
      logger.error("Inventory restock failed for order");
      // SEND KAFKA EVENT BACK TO ORDER SERVICE
    }
  }



}

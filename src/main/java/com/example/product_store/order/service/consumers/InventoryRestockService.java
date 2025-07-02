package com.example.product_store.order.service.consumers;

import com.example.product_store.order.events.InventoryCompletedEvent;
import java.time.LocalDateTime;

import com.example.product_store.order.service.actions.RollBackInventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryRestockService {

  public static final Logger logger =
      LoggerFactory.getLogger(InventoryRestockService.class);

  private final RollBackInventoryService rollBackInventoryService;

  public InventoryRestockService(RollBackInventoryService rollBackInventoryService) {
    this.rollBackInventoryService = rollBackInventoryService;
  }

  // CLASS WILL ACT AS A WRAPPER FOR THE REDUCTION SERVICE
  // SO CAN USE KAFKA + @TRANSACTIONAL FOR SAFER REDUCTION
  // Receive inventory completed event from Saga Orchestra
  // PAYMENT FAILED -> RESTOCK INVENTORY
  @KafkaListener(topics = "payment-failed", groupId = "saga-group")
  public void execute(InventoryCompletedEvent inventoryCompletedEvent) {
    logger.info(
        "Received Order at RollBackService. Time of receipt:{}", LocalDateTime.now());

    try {
      boolean result = rollBackInventoryService.execute(inventoryCompletedEvent);
      if (result) {
        String message = "Successfully restocked inventory";
        logger.info(message);
      }
    } catch (Exception ex) {
      logger.error("Inventory restock failed due to the following: {}",ex.getMessage());

    }
  }
}

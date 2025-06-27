package com.example.product_store.kafka.service;

import com.example.product_store.kafka.enums.InventoryStatus;
import com.example.product_store.kafka.events.InventoryCompletedEvent;
import com.example.product_store.kafka.events.StartInventoryEvent;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryReservationService {

  private final KafkaTemplate<String, InventoryCompletedEvent> kafkaTemplate;

  public static final Logger logger = LoggerFactory.getLogger(InventoryReservationService.class);
  private final InventoryReductionService inventoryReductionService;

  public InventoryReservationService(
      KafkaTemplate<String, InventoryCompletedEvent> kafkaTemplate,
      InventoryReductionService inventoryReductionService) {
    this.kafkaTemplate = kafkaTemplate;
    this.inventoryReductionService = inventoryReductionService;
  }

  @KafkaListener(topics = "inventory-commands", groupId = "saga-group")
  public void execute(StartInventoryEvent startInventoryEvent) {
    logger.info(
        "Received Order at Inventory service:{}. Time of receipt:{}",
        startInventoryEvent.getOrderId(),
        LocalDateTime.now());

    try {
      boolean result = inventoryReductionService.execute(startInventoryEvent);
      if (result) {
        String message = "Successfully deducted stock from inventory";
        InventoryCompletedEvent successEvent =
            new InventoryCompletedEvent(
                startInventoryEvent, InventoryStatus.SUCCESS, message);
        // SEND KAFKA EVENTS IF NO ERRORS CAUGHT
        kafkaTemplate.send("inventory-events", successEvent);
      }
    } catch (Exception ex) {
      logger.error("Inventory reduction failed for order");

      InventoryCompletedEvent failedEvent =
          new InventoryCompletedEvent(
              startInventoryEvent,
              InventoryStatus.FAILED,
              "Inventory error: " + ex.getMessage());
      kafkaTemplate.send("inventory-events", failedEvent);
    }
  }
}

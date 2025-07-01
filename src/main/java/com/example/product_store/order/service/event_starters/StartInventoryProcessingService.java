package com.example.product_store.order.service.event_starters;

import com.example.product_store.order.enums.InventoryStatus;
import com.example.product_store.order.events.InventoryCompletedEvent;
import com.example.product_store.order.events.StartInventoryEvent;
import java.time.LocalDateTime;

import com.example.product_store.order.service.processing.InventoryReductionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StartInventoryProcessingService {

  private final KafkaTemplate<String, InventoryCompletedEvent> kafkaTemplate;

  public static final Logger logger = LoggerFactory.getLogger(StartInventoryProcessingService.class);
  private final InventoryReductionService inventoryReductionService;

  public StartInventoryProcessingService(
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
      // will run another service with @transactional to lock the db
      // and deduct stock
      // if successful, result should be true
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
      logger.warn("Inventory reduction failed, sending failed event to Kafka");
      InventoryCompletedEvent failedEvent =
          new InventoryCompletedEvent(
              startInventoryEvent,
              InventoryStatus.FAILED,
              "Inventory error: " + ex.getMessage());
      kafkaTemplate.send("inventory-events", failedEvent);
    }
  }
}

package com.example.product_store.order;

import com.example.product_store.order.enums.InventoryStatus;
import com.example.product_store.order.enums.OrderStatus;
import com.example.product_store.order.enums.PaymentStatus;
import com.example.product_store.order.events.*;
import com.example.product_store.order.service.InventoryRestockService;
import com.example.product_store.order.service.RollBackPayment;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaOrchestra {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  // type safe hashmap to enable concurrency
  private final Map<String, SagaEvents> sagaEvents = new ConcurrentHashMap<>();

  private final RollBackPayment rollBackPayment;
  public static final Logger logger = LoggerFactory.getLogger(SagaOrchestra.class);
  private final InventoryRestockService inventoryRestockService;

  public SagaOrchestra(
      KafkaTemplate<String, Object> kafkaTemplate,
      RollBackPayment rollBackPayment,
      InventoryRestockService inventoryRestockService) {
    this.kafkaTemplate = kafkaTemplate;
    this.rollBackPayment = rollBackPayment;
    this.inventoryRestockService = inventoryRestockService;
  }

  // CONSUME ORDER EVENT FIRST
  @KafkaListener(topics = "order-commands", groupId = "saga-group")
  public void handlerOrderEvent(OrderCreatedEvent event) {
    logger.info(
        "Received Order at Saga Orchestra. Time of receipt:{}", LocalDateTime.now());

    // SENDS PAYMENT EVENT -> PAYMENT SERVICE
    StartPaymentEvent startPaymentEvent = new StartPaymentEvent(event);
    kafkaTemplate.send("payment-commands", startPaymentEvent);

    // AT THE SAME TIME -> START INVENTORY SERVICE
    StartInventoryEvent startInventoryEvent = new StartInventoryEvent(event);
    kafkaTemplate.send("inventory-commands", startInventoryEvent);
  }

  // CONSUME PAYMENT RESPONSE
  @KafkaListener(topics = "payment-events", groupId = "saga-group")
  public void handlePaymentCompletion(PaymentCompletedEvent paymentCompletedEvent) {
    logger.info("Completed payment status: {} ", paymentCompletedEvent);

    SagaEvents events =
        sagaEvents.computeIfAbsent(
            paymentCompletedEvent.getOrderId(),
            k -> new SagaEvents(paymentCompletedEvent.getOrderId()));
    events.setPaymentCompletedEvent(paymentCompletedEvent);
    checkSagaCompletion(events);
  }

  // CONSUME INVENTORY RESPONSE
  @KafkaListener(topics = "inventory-events", groupId = "saga-group")
  public void handleInventoryCompletion(InventoryCompletedEvent inventoryCompletedEvent) {
    logger.info("Completed inventory status: {} ", inventoryCompletedEvent.getStatus());

    SagaEvents events =
        sagaEvents.computeIfAbsent(
            inventoryCompletedEvent.getOrderId(),
            k -> new SagaEvents(inventoryCompletedEvent.getOrderId()));
    events.setInventoryCompletedEvent(inventoryCompletedEvent);
    checkSagaCompletion(events);
  }

  private void checkSagaCompletion(SagaEvents events) {
    OrderStatus status;
    String message = "";
    try {
      PaymentCompletedEvent paymentEvent = events.getPaymentCompletedEvent();
      InventoryCompletedEvent inventoryEvent = events.getInventoryCompletedEvent();

      if (paymentEvent == null || inventoryEvent == null) {
        // One or both events not yet received, just wait
        logger.info(
            "Waiting for both payment and inventory events for order: {}",
            events.getOrderId());
        return;
      }

      if (events.isBothCompletedSuccessfully()) {
        logger.info("Both events completed successfully, returning to OrderService");
        // Change status to success
        status = OrderStatus.SUCCESS;
        message = "Order completed processing successfully";
      } else if (events.getPaymentStatus() == PaymentStatus.DENIED
          && events.getInventoryStatus() == InventoryStatus.SUCCESS) {
        logger.info("Stock deducted but payment failed, rolling back inventory");
        inventoryRestockService.execute(inventoryEvent);
        status = OrderStatus.FAILED;
        message = "Order processing failed due to insufficient balance in user account";

      } else if (events.getPaymentStatus() == PaymentStatus.SUCCESS
          && events.getInventoryStatus() == InventoryStatus.FAILED) {
        logger.info("Payment completed but stock reservation failed, refunding customer");
        rollBackPayment.execute(
            events.getPaymentCompletedEvent()); // This is likely throwing
        status = OrderStatus.FAILED;
        message = "Order processing failed due to insufficient stock";
      } else {
        logger.info("Both events failed, ending operation in Saga Orchestra");
        status = OrderStatus.FAILED;
        message = "Order processing failed due to insufficient stock and failed payment";
      }

      kafkaTemplate.send(
          "order-events", new OrderCompletionEvent(events.getOrderId(), status, message));

    } catch (Exception e) {
      logger.error("Saga orchestration error: {}", e.getMessage(), e);
      // Optionally send a failure event to dead-letter topic or audit log
      message = "Order processing failed due to server error";
      kafkaTemplate.send(
          "order-events",
          new OrderCompletionEvent(events.getOrderId(), OrderStatus.FAILED, message));
    }
  }
}

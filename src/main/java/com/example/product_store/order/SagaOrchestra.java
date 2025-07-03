package com.example.product_store.order;

import com.example.product_store.order.enums.InventoryStatus;
import com.example.product_store.order.enums.OrderStatus;
import com.example.product_store.order.enums.PaymentStatus;
import com.example.product_store.order.events.*;

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


  public static final Logger logger = LoggerFactory.getLogger(SagaOrchestra.class);


  public SagaOrchestra(
      KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;

  }

  // CONSUME ORDER EVENT FIRST
  @KafkaListener(topics = "order-commands", groupId = "saga-group")
  public void handlerOrderEvent(OrderCreatedEvent event) {
    logger.info(
        "Received Order at Saga Orchestra. Time of receipt:{}", LocalDateTime.now());

    // START OF ORDER PROCESSING
    // SENDS PAYMENT EVENT -> START PAYMENT SERVICE
    StartPaymentEvent startPaymentEvent = new StartPaymentEvent(event);
    kafkaTemplate.send("payment-commands", startPaymentEvent);

    // OUTBOX MESSSAGE PATTERN
    // IDEMPOTENCY -> REMOVE SAGA CONSISTENCY
    // PRIMARY KEY IN PAYMENT TABLE
    // BLOOM FILTER
    // AT THE SAME TIME -> START INVENTORY SERVICE
    StartInventoryEvent startInventoryEvent = new StartInventoryEvent(event);
    kafkaTemplate.send("inventory-commands", startInventoryEvent);
  }

  // CONSUME PAYMENT RESPONSE
  // Store the payment event under SagaEvents Map
  // Will be retrieved with order id as key
  @KafkaListener(topics = "payment-events", groupId = "saga-group")
  public void handlePaymentCompletion(PaymentCompletedEvent paymentCompletedEvent) {
    logger.info("Completed payment status: {} ", paymentCompletedEvent);

    // RACE CONDITION: IF PAYMENT COMPLETES FIRST
    // IF ORDER ID IS NOT PRESENT IN CONCURRENT HASHMAP
    // CREATE A NEW SAGA EVENT
    // SET THE orderId:{SagaEvents}, set paymentCompletedEvent
    SagaEvents events =
        sagaEvents.computeIfAbsent(
            paymentCompletedEvent.getOrderId(),
            k -> new SagaEvents(paymentCompletedEvent.getOrderId()));
    events.setPaymentCompletedEvent(paymentCompletedEvent);
    checkSagaCompletion(events);
  }


  // CONSUME INVENTORY RESPONSE
  // Store the payment event under SagaEvents Map
  // Will be retrieved with order id as key
  @KafkaListener(topics = "inventory-events", groupId = "saga-group")
  public void handleInventoryCompletion(InventoryCompletedEvent inventoryCompletedEvent) {
    logger.info("Completed inventory status: {} ", inventoryCompletedEvent.getStatus());

    // RACE CONDITION: IF INVENTORY EVENT COMPLETES FIRST
    // IF ORDER ID IS NOT PRESENT IN CONCURRENT HASHMAP
    // CREATE A NEW SAGA EVENT
    // SET THE orderId:{SagaEvents}, set InventoryCompletedEvent
    SagaEvents events =
        sagaEvents.computeIfAbsent(
            inventoryCompletedEvent.getOrderId(),
            k -> new SagaEvents(inventoryCompletedEvent.getOrderId()));
    events.setInventoryCompletedEvent(inventoryCompletedEvent);
    checkSagaCompletion(events);
  }

  // CHECK SAGA COMPLETION
  private void checkSagaCompletion(SagaEvents events) {
    OrderStatus status;
    String message = "";
    try {
      PaymentCompletedEvent paymentEvent = events.getPaymentCompletedEvent();
      InventoryCompletedEvent inventoryEvent = events.getInventoryCompletedEvent();

      // Will get triggered since 1 event might take slower to complete than the other
      if (paymentEvent == null || inventoryEvent == null) {
        // One or both events not yet received, wait before processing below
        logger.info(
            "Waiting for both payment and inventory events for order at {}",
            LocalDateTime.now());
        return;
      }

      // if both events completed successfully
      if (events.isBothCompletedSuccessfully()) {
        logger.info("Both events completed successfully, returning to OrderService");
        // Change status to success
        status = OrderStatus.SUCCESS;
        message = "Order completed processing successfully";
      }

      // If payment status is denied and inventory status is success
      // restock inventory
      // user don't have to refund since nothing is deducted
      else if (events.getPaymentStatus() == PaymentStatus.DENIED
          && events.getInventoryStatus() == InventoryStatus.SUCCESS) {
        logger.info("Stock deducted but payment failed, rolling back inventory");
        status = OrderStatus.FAILED;
        message = "Order processing failed due to insufficient balance in user account";
        // Since payment event failed -> roll back inventory
        kafkaTemplate.send("payment-failed",inventoryEvent);
      }

      // If payment is success and inventory status is denied, refund the user.
      // don't have to restock since stock is not deducted
      // if stock runs out during deduction operation, everything is rolled back with transactional
      else if (events.getPaymentStatus() == PaymentStatus.SUCCESS
          && events.getInventoryStatus() == InventoryStatus.FAILED) {
        logger.info("Payment completed but stock reservation failed, refunding customer");
        status = OrderStatus.FAILED;
        message = "Order processing failed due to insufficient stock in the inventory";
        // Since inventory event failed - > send payment event for refund
        kafkaTemplate.send("inventory-failed",paymentEvent);
      }
      // both events failed, do nothing since db is not affected
      else {
        logger.info("Both events failed, ending operation in Saga Orchestra");
        status = OrderStatus.FAILED;
        message = "Order processing failed due to insufficient stock and failed payment";
      }

      // update order processing status
      kafkaTemplate.send(
          "order-events", new OrderCompletionEvent(
                  events.getOrderId(),
                      paymentEvent.getCustomerId(),status, message, inventoryEvent.getPurchasesMap()));

    } catch (Exception e) {
      logger.error("Saga orchestration error: {}", e.getMessage(), e);
      // Optionally send a failure event to dead-letter topic or audit log
      message = "Order processing failed due to server error";
      kafkaTemplate.send(
          "order-events",
          new OrderCompletionEvent(events.getOrderId(),
                  events.getPaymentCompletedEvent().getCustomerId()
                  ,OrderStatus.FAILED, message,events.getInventoryCompletedEvent().getPurchasesMap()));
    }
  }
}

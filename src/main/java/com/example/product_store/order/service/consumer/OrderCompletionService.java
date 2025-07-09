package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCompletionService {

  private final Logger logger = LoggerFactory.getLogger(OrderCompletionService.class);

  private final OrderRepository orderRepository;
  private final ConcurrentHashMap<String, EventAccumulator> orderStatusMap = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final OutboxRepository outboxRepository;

  public OrderCompletionService(OrderRepository orderRepository, OutboxRepository outboxRepository) {
    this.orderRepository = orderRepository;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @KafkaListener(topics = "order-completed.events", groupId = "order-completion-consumer")
  public void execute(String message) {
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, skipping.");
      return;
    }

    try {
      // Parse the message from debezium
      OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);
      String orderId = receipt.getOrderId();
      String eventType = receipt.getEventType();
      // Payload will be transferred later for completion / failure
      EventPayload payload = new EventPayload(receipt);
      logger.info("Received event for order {}: {}", orderId, eventType);

      // FOR CONCURRENCY
      // When the first process completes
      // save the order id as a key for concurrent hashmap
      orderStatusMap.putIfAbsent(orderId, new EventAccumulator());
      // set the order event type -> accumulator status
      // used for comparison
      EventAccumulator accumulator = orderStatusMap.get(orderId);

      switch (eventType) {
        case "PaymentAccepted":
        case "PaymentDenied":
          accumulator.setPaymentStatus(eventType);
          break;
        case "InventoryReserved":
        case "InventoryFailed":
          accumulator.setInventoryStatus(eventType);
          break;
        default:
          logger.warn("Unknown event type received: {}", eventType);
          return;
      }

      // when accumulator is not read, the data below will be thrown off
      if (!accumulator.isReady()) {
        logger.info("Waiting for both events for order {}. Current state: {}", receipt.getOrderId() , accumulator);
        return;
      }

      // Now act based on the combined event result
      String payment = accumulator.getPaymentStatus();
      String inventory = accumulator.getInventoryStatus();
      switch (payment + "-" + inventory) {
        case "PaymentAccepted-InventoryFailed":
          logger.info("Inventory out of stock. Triggering refund service for user");
          sendOutboxEvent("payment-refund", "InventoryFailed", orderId, payload);
          updateOrderStatus(orderId, "Failure", "Order processing failed due to Inventory Failure");
          break;

        case "PaymentDenied-InventoryReserved":
          logger.info("Payment denied. Triggering inventory restock service");
          sendOutboxEvent("inventory-restock", "PaymentDenied", orderId, payload);
          updateOrderStatus(orderId, "Failure", "Order processing failed as Payment is Denied");
          break;

          // BOTH SUCCESS
        case "PaymentAccepted-InventoryReserved":
          logger.info("Both services succeeded. Completing order");
          // WILL NEED TO SEND ADMIN MESSAGES VIA OUTBOX EVENT
          updateOrderStatus(orderId, "Success", "Order processing completed successfully");
          break;

        default:
          logger.info("Both services failed. No further actions needed");
          updateOrderStatus(orderId, "Failure", "Order processing failed: Payment denied and Inventory out of stock");
          break;
      }

      orderStatusMap.remove(orderId);

    } catch (Exception ex) {
      logger.warn("Order Completion failed due to the following reason: {}", ex.getMessage());
    }
  }

  // Continue letting kafka send events
  private void sendOutboxEvent(String aggregateType, String eventType, String orderId, EventPayload payload) {
    try {
      Outbox outbox = new Outbox(
              null,
              aggregateType,
              orderId,
              eventType,
              objectMapper.writeValueAsString(payload)
      );
      outboxRepository.save(outbox);
    } catch (Exception ex) {
      logger.error("Failed to write outbox event for {}-{}: {}", aggregateType, eventType, ex.getMessage(), ex);
    }
  }

  private void updateOrderStatus(String orderId, String status, String message) {
    orderRepository.findById(orderId).ifPresent(order -> {
      order.setOrderStatus(status);
      order.setMessage(message);
      Order savedOrder = orderRepository.save(order);
      logger.info("Updated order status: {}", savedOrder);
    });
  }

}



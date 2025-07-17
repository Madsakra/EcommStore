package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.outbox_event.EventAccumulator;
import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  public void execute(String message) throws JsonProcessingException {
    // if message is empty
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, throwing empty kafka message exception.");
      throw new EmptyKafkaMessageException();
    }

    // Parse the message from debezium
    OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);
    String orderId = receipt.getOrderId();

    // ENSURE THAT EVENT TYPE IS RELATED TO PAYMENT / INVENTORY
    // HARDCODED CODES
    // WILL BE PLACED IN SWITCH CASE TO SET THE STATUS LATER ON
    String eventType = receipt.getEventType();
    logger.info("Received event for order {}: {}", orderId, eventType);

    // Payload will be transferred later for completion / failure
    EventPayload payload = new EventPayload(receipt);

    // FOR CONCURRENCY
    // When the first process completes
    // save the order id as a key for concurrent hashmap
    orderStatusMap.putIfAbsent(orderId, new EventAccumulator());

    // set the order event type -> accumulator status
    // used for comparison
    EventAccumulator accumulator = orderStatusMap.get(orderId);

    // SET THE ACCUMULATOR STATUS
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
        throw new EmptyKafkaMessageException();
    }

    // when accumulator is not read, will just cut off here, allow the next event to come in and process
    if (!accumulator.isReady()) {
      logger.info("Waiting for both events for order {}. Current state: {}", receipt.getOrderId(), accumulator);
      return;
    }

    // CHECK THE STATUS OF BOTH EVENTS
    // COMBINE TO STRING FOR SWITCH CASE
    String payment = accumulator.getPaymentStatus();
    String inventory = accumulator.getInventoryStatus();

    switch (payment + "-" + inventory) {

      // PAYMENT ACCEPTED BY INVENTORY EVENT FAILED
      // RECOVERY ACTION: REFUND USER
      case "PaymentAccepted-InventoryFailed":
        logger.info("Inventory out of stock. Triggering refund service for user");
        sendOutboxEvent("payment-refund", "InventoryFailed", orderId, payload);
        updateOrderStatus(
            orderId, "Failure", "Order processing failed due to Inventory Failure / Product out of stock.");
        break;

      // PAYMENT DENIED BUT INVENTORY RESERVED
      // RECOVERY ACTION: RESTOCK INVENTORY
      case "PaymentDenied-InventoryReserved":
        logger.info("Payment denied. Triggering inventory restock service");
        sendOutboxEvent("inventory-restock", "PaymentDenied", orderId, payload);
        updateOrderStatus(
            orderId,
            "Failure",
            "Order processing failed as Payment is denied due to insufficient funds on your account. Please top up more"
                + " funds to use your current wallet.");
        break;

      // BOTH SUCCESS
      // UPDATE THE ORDER TABLE
      // SEND MESSAGE OUT TO ADMINS
      case "PaymentAccepted-InventoryReserved":
        logger.info("Both services succeeded. Completing order");
        // UPDATE ORDER SUCCESS
        sendOutboxEvent("notify-admin", "NotifyAdmin", orderId, payload);
        updateOrderStatus(orderId, "Success", "Order processing completed successfully");
        break;

      // IF SOMETHING GOES WRONG
      // UPDATE ORDER TABLE (ORDER FAILED)
      default:
        logger.info("Both services failed. No further actions needed");
        updateOrderStatus(orderId, "Failure", "Order processing failed: Payment denied due to " +
                "insufficient balance in wallet and Inventory out of stock");
        break;
    }

    orderStatusMap.remove(orderId);
  }

  // Continue letting kafka send events
  private void sendOutboxEvent(String aggregateType, String eventType, String orderId, EventPayload payload)
      throws JsonProcessingException {

    Outbox outbox = new Outbox(null, aggregateType, orderId, eventType, objectMapper.writeValueAsString(payload));
    outboxRepository.save(outbox);
  }

  private void updateOrderStatus(String orderId, String status, String message) {
    orderRepository
        .findById(orderId)
        .ifPresent(
            order -> {
              order.setOrderStatus(status);
              order.setMessage(message);
              Order savedOrder = orderRepository.save(order);
              logger.info("Updated order status: {}", savedOrder);
            });
  }
}

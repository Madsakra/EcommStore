package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryReservationService {

  public static final Logger logger = LoggerFactory.getLogger(InventoryReservationService.class);
  private final ProductRepository productRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final OutboxRepository outboxRepository;

  public InventoryReservationService(ProductRepository productRepository, OutboxRepository outboxRepository) {
    this.productRepository = productRepository;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @KafkaListener(topics = "order.events", groupId = "order-service-consumer")
  public void execute(String message) throws JsonProcessingException {

    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, throwing empty kafka message exception.");
      throw new EmptyKafkaMessageException("Received null or empty Kafka message, throwing empty kafka message exception.");
    }

    // EXTRACT THE OUTBOX EVENT
    OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);

    // SHORT CIRCUIT EARLY IF EVENT TYPE IS NOT RIGHT
    if (!"OrderCreated".equals(receipt.getEventType())) {
      return;
    }

    // EVENT PAYLOAD THAT IS USED FOR TRANSFER TO OUTBOX TABLE
    // IN EVENT OF FAILURE / SUCCESS
    EventPayload payload = new EventPayload(receipt);

    // CHECK THE PAYLOAD FOR ANY ERRORS
    OutboxEventUtil.orderCreatedPayloadValidator(receipt);

    logger.info("Verified Outbox Event Payload :{}", receipt);

    // GET ALL THE PRODUCT id's in a map (used for findAll in repository)
    List<String> productIds = receipt.getOrderCreationRequests().stream().map(OrderCreationRequest::getId).toList();

    // GET LATEST UPDATED PRODUCTS
    // TRIGGER PESSIMISTIC LOCK
    List<Product> lockedProducts = productRepository.findAllById(productIds);

    // Map of locked products by ID for fast lookup
    Map<String, Product> lockedProductMap =
        lockedProducts.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

    // PHASE 1: VALIDATION
    for (OrderCreationRequest request : receipt.getOrderCreationRequests()) {
      Product product = lockedProductMap.get(request.getId());

      // WHEN: UNABLE TO FIND PRODUCT
      if (product == null) {
        logger.warn("Product Not found for :{}", request.getId());
        // Insert into outbox event to signal failure
        sendOutboxEvent("InventoryFailed", receipt.getOrderId(), payload);
        return;
      }

      // WHEN: STOCK < DESIRED QUANTITY
      if (product.getStock() < request.getQuantity()) {
        logger.warn("Insufficient stock for : {}", product.getId());
        // Insert into outbox event to signal failure
        sendOutboxEvent("InventoryFailed", receipt.getOrderId(), payload);
        return;
      }
    }

    // Phase 2: All validations passed, now deduct stock
    for (OrderCreationRequest request : receipt.getOrderCreationRequests()) {
      Product product = lockedProductMap.get(request.getId());
      product.setStock(product.getStock() - request.getQuantity());
    }

    // TAKES THE WHOLE LIST OF LOCKED PRODUCTS (EDITED ONES)
    // SAVE THEM IN REPO
    List<Product> savedProducts = productRepository.saveAll(lockedProducts);
    logger.info("Successfully saved products. After changes: {}", savedProducts);

    // Insert into outbox
    // SIGNAL SUCCESS
    sendOutboxEvent("InventoryReserved", receipt.getOrderId(), payload);
  }

  // Continue letting kafka send events
  private void sendOutboxEvent(String eventType, String orderId, EventPayload payload) throws JsonProcessingException {
    Outbox outbox = new Outbox(null, "order-completed", orderId, eventType, objectMapper.writeValueAsString(payload));
    Outbox savedOutbox = outboxRepository.save(outbox);
    logger.info("Successfully saved outbox event :{}", savedOutbox);
  }
}

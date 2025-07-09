package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
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

  public static final Logger logger =
      LoggerFactory.getLogger(InventoryReservationService.class);
  private final ProductRepository productRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final OutboxRepository outboxRepository;

  public InventoryReservationService(
      ProductRepository productRepository, OutboxRepository outboxRepository) {
    this.productRepository = productRepository;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @KafkaListener(topics = "order.events", groupId = "order-service-consumer")
  public void execute(String message) {
    logger.info(message);
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, skipping.");
      return;
    }
    try {
      // Extract "after" field (actual outbox row)
      OutboxEventReceipt eventDTO = OutboxEventUtil.extractOutboxEvent(message);
      logger.info("Event dto is :{}", eventDTO);
      // ONLY CONTINUE PARSING IF THE MESSAGE TYPE IS CORRECT
      String messageType = "OrderCreated";

      // SEND THE PAYLOAD LATER TO COMPLETION / FAILURE EVENT
      EventPayload payload = new EventPayload(eventDTO);

      // ONLY PICK UP ORDER CREATED EVENTS
      if (messageType.equals(eventDTO.getEventType())) {
        // CHECK THE PAYLOAD FOR ANY ERRORS
        OutboxEventUtil.orderCreatedPayloadValidator(eventDTO);

        List<String> productIds =
            eventDTO.getOrderCreationRequests().stream()
                .map(OrderCreationRequest::getId)
                .toList();

        // GET LATEST UPDATED PRODUCTS
        // TRIGGER PESSIMISTIC LOCK
        List<Product> lockedProducts = productRepository.findAllById(productIds);

        // Map of locked products by ID for fast lookup
        Map<String, Product> lockedProductMap =
            lockedProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (OrderCreationRequest request : eventDTO.getOrderCreationRequests()) {
          Product product = lockedProductMap.get(request.getId());
          if (product == null) {
            logger.warn("Product Not found for :{}", request.getId());
            // Insert into outbox
            Outbox outbox =
                    new Outbox(
                            null,
                            "order-completed",
                            eventDTO.getOrderId(),
                            "InventoryFailed",
                            objectMapper.writeValueAsString(payload));
            outboxRepository.save(outbox);
            throw new ProductStockException("Product not found: " + request.getId());
          }
          if (product.getStock() < request.getQuantity()) {
            logger.warn("Insufficient stock for : {}", product.getId());
            Outbox outbox =
                    new Outbox(
                            null,
                            "order-completed",
                            eventDTO.getOrderId(),
                            "InventoryFailed",
                            objectMapper.writeValueAsString(payload));
            outboxRepository.save(outbox);
            throw new ProductStockException("Insufficient stock for: " + product.getId());
          }
          product.setStock(product.getStock() - request.getQuantity());
        }

        List<Product> savedProducts = productRepository.saveAll(lockedProducts);
        logger.info("Successfully saved products. After changes: {}", savedProducts);

        // Insert into outbox
        Outbox outbox =
            new Outbox(
                null,
                "order-completed",
                eventDTO.getOrderId(),
                "InventoryReserved",
                objectMapper.writeValueAsString(payload));
        outboxRepository.save(outbox);
      }

    } catch (Exception e) {
      logger.warn("Encountered exception, throwing it: {}", e.getMessage());
    }
  }
}

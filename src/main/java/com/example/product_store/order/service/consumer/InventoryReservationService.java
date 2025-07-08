package com.example.product_store.order.service.consumer;


import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.outbox_event.InventoryCompletedPayload;
import com.example.product_store.order.dto.outbox_event.OrderCreatedPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventDTO;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.model.OutboxEvent;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public InventoryReservationService(ProductRepository productRepository, OutboxRepository outboxRepository) {
    this.productRepository = productRepository;
        this.outboxRepository = outboxRepository;
    }

  @Transactional
  @KafkaListener(
      topics = "store.product_store.outbox_event",
      groupId = "order-service-consumer")
  public void execute(String message) {

    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, skipping.");
      return;
    }
    try {
      // Extract "after" field (actual outbox row)
      OutboxEventDTO eventDTO = OutboxEventUtil.extractOutboxEvent(message);
      // ONLY CONTINUE PARSING IF THE MESSAGE TYPE IS CORRECT
        String messageType = "OrderCreated";
        if (Objects.equals(eventDTO.getType(), messageType))
      {
        // Parse order payload
        OrderCreatedPayload orderCreated =
                objectMapper.readValue(eventDTO.getPayload(), OrderCreatedPayload.class);
        logger.info(
                "Order id is :{}, belongs to client:{}. Products ordered :{}",
                orderCreated.getOrderId(),
                orderCreated.getCustomerId(),
                orderCreated.getOrderCreationRequests());

        // CHECK THE PAYLOAD FOR ANY ERRORS
        OutboxEventUtil.orderCreatedPayloadValidator(orderCreated);

        List<String> productIds =
                orderCreated.getOrderCreationRequests().stream()
                        .map(OrderCreationRequest::getId)
                        .toList();

        // GET LATEST UPDATED PRODUCTS
        // TRIGGER PESSIMISTIC LOCK
        List<Product> lockedProducts = productRepository.findAllById(productIds);

        // Map of locked products by ID for fast lookup
        Map<String, Product> lockedProductMap =
                lockedProducts.stream()
                        .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (OrderCreationRequest request : orderCreated.getOrderCreationRequests()) {
          Product product = lockedProductMap.get(request.getId());
          if (product == null) {
            logger.warn("Product Not found for :{}", request.getId());
            throw new ProductStockException("Product not found: " + request.getId());
          }
          if (product.getStock() < request.getQuantity()) {
            logger.warn("Insufficient stock for : {}", product.getId());
            throw new ProductStockException("Insufficient stock for: " + product.getId());
          }
          product.setStock(product.getStock() - request.getQuantity());
        }

        List<Product> savedProducts = productRepository.saveAll(lockedProducts);
        logger.info("Successfully saved products. After changes: {}", savedProducts);


        // Create event
        InventoryCompletedPayload payload =
                new InventoryCompletedPayload(
                        orderCreated.getOrderId(),
                        "InventoryReserved",
                        orderCreated.getOrderCreationRequests());

        // Insert into outbox
        OutboxEvent outboxEvent =
                new OutboxEvent(
                        null,
                        "Inventory",
                        orderCreated.getOrderId(),
                        "InventoryReserved",
                        objectMapper.writeValueAsString(payload));
        outboxRepository.save(outboxEvent);

      }

    } catch (Exception e) {
      logger.warn("Encountered exception, throwing it: {}", e.getMessage());
    }
  }
}

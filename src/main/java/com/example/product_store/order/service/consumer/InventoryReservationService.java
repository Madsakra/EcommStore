package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.outbox_event.OrderCreatedPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventDTO;
import com.example.product_store.order.dto.outbox_event.OutboxEventWrapper;
import com.example.product_store.order.exceptions.OrderPayloadMalformedException;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryReservationService {

    public static final Logger logger = LoggerFactory.getLogger(InventoryReservationService.class);
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InventoryReservationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "store.product_store.outbox_event",
            groupId = "order-service-consumer")
    public void execute(String message)
    {
        try {
            // 1. Parse debezium outer json wrapper
            OutboxEventWrapper wrapper =
                    objectMapper.readValue(message, OutboxEventWrapper.class);
            // 2. Extract "after" field (actual outbox row)
            OutboxEventDTO eventDTO = wrapper.getPayload().getAfter();
            // Log the metadata
            logger.info("Parsed message from kafka: {}", eventDTO);

            // Parse order payload
            OrderCreatedPayload orderCreated =
                    objectMapper.readValue(eventDTO.getPayload(), OrderCreatedPayload.class);
            logger.info(
                    "Order id is :{}, belongs to client:{}. Products ordered :{}",
                    orderCreated.getOrderId(),
                    orderCreated.getCustomerId(),
                    orderCreated.getOrderCreationRequests());

            // CHECK THE PAYLOAD FOR ANY ERRORS
            if (orderCreated.getOrderId() == null || orderCreated.getOrderId().isEmpty()){
                logger.warn("Current order payload has no order id");
                throw new OrderPayloadMalformedException("Current order does not have an order id tied to it");
            }

            if (orderCreated.getCustomerId() == null || orderCreated.getCustomerId().isEmpty()) {
                logger.warn("Current user :{}, is null / empty",orderCreated.getCustomerId());
                throw new OrderPayloadMalformedException("Current order does not have a customer tied to it");
            }

            if (orderCreated.getTotalPrice().compareTo(BigDecimal.ZERO)<0){
                logger.warn("Order has a negative sum tied to it :{}",orderCreated.getTotalPrice());
                throw new OrderPayloadMalformedException("Current order's tabulated costs is negative.");
            }

            List<String> productIds = orderCreated.getOrderCreationRequests().stream().map(OrderCreationRequest::getId).toList();

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
                    logger.warn("Product Not found for :{}",request.getId());
                    throw new ProductStockException("Product not found: " + request.getId());
                }
                if (product.getStock() < request.getQuantity()) {
                    logger.warn("Insufficient stock for : {}",product.getId());
                    throw new ProductStockException("Insufficient stock for: " + product.getId());
                }
                product.setStock(product.getStock() - request.getQuantity());
            }

            List<Product> savedProducts = productRepository.saveAll(lockedProducts);
            logger.info("Successfully saved products. After changes: {}",savedProducts);


        } catch (Exception e) {
            logger.warn("Encountered exception, throwing it: {}", e.getMessage());
        }
    }

}

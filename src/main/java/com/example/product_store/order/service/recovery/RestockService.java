package com.example.product_store.order.service.recovery;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.util.OutboxEventUtil;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RestockService {

    private final ProductRepository productRepository;
    public Logger logger = LoggerFactory.getLogger(RestockService.class);

    public RestockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @KafkaListener(topics="inventory-restock.events",groupId = "inventory-restock-consumer")
    public void execute(String message){
        // if message is empty
        if (message == null || message.isBlank()) {
            logger.warn("Received null or empty Kafka message, throwing empty kafka message exception.");
            throw new EmptyKafkaMessageException("The current kafka message is empty");
        }

        try {
            // EXTRACT THE OUTBOX EVENT
            OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);

            // THE REQUIRED MESSAGE TYPE FOR THIS SERVICE
            String messageType = "PaymentDenied";

            // ONLY PICK UP ORDER CREATED EVENTS
            if (messageType.equals(receipt.getEventType())) {

                // CHECK THE PAYLOAD FOR ANY ERRORS
                OutboxEventUtil.orderCreatedPayloadValidator(receipt);

                logger.info("Received outbox event at restock service. Verified Outbox Event Payload :{}", receipt);

                // GET ALL THE PRODUCT id's in a map (used for findAll in repository)
                List<String> productIds = receipt.getOrderCreationRequests().stream().map(OrderCreationRequest::getId).toList();

                // GET LATEST UPDATED PRODUCTS
                // TRIGGER PESSIMISTIC LOCK
                List<Product> lockedProducts = productRepository.findAllById(productIds);

                // Map of locked products by ID for fast lookup
                Map<String, Product> lockedProductMap =
                        lockedProducts.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

                // LOOP THROUGH LIST OF ORDER REQUEST
                for (OrderCreationRequest request : receipt.getOrderCreationRequests()) {
                    Product product = lockedProductMap.get(request.getId());

                    // WHEN: UNABLE TO FIND PRODUCT
                    if (product == null) {
                        logger.warn("Product Not found for :{}", request.getId());
                        throw new ProductStockException("Product not found: " + request.getId());
                    }

                    // IF STOCK MANAGES TO PASS CHECKS
                    // ADD THEM BACK
                    product.setStock(product.getStock() + request.getQuantity());
                }

                // TAKES THE WHOLE LIST OF LOCKED PRODUCTS (EDITED ONES)
                // SAVE THEM IN REPO
                 productRepository.saveAll(lockedProducts);
                logger.info("Successfully restocked products.");

            }

        } catch (Exception e) {
            // USED TO CATCH JSON PARSING ERROR
            logger.warn("Encountered exception, throwing it: {}", e.getMessage());
        }
    }


}



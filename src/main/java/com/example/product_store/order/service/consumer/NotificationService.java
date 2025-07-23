package com.example.product_store.order.service.consumer;

import com.example.product_store.notification.model.Notification;
import com.example.product_store.notification.repositories.NotificationRepository;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.exceptions.WrongKafkaEventException;
import com.example.product_store.order.service.ProductRetrievalService;
import com.example.product_store.order.util.OutboxEventUtil;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final ProductRetrievalService productRetrievalService;
  private Logger logger = LoggerFactory.getLogger(NotificationService.class);

  public NotificationService(
      NotificationRepository notificationRepository, ProductRetrievalService productRetrievalService) {
    this.notificationRepository = notificationRepository;
    this.productRetrievalService = productRetrievalService;
  }

  @Transactional
  @KafkaListener(topics = "notify-admin.events", groupId = "notify-admin-consumer")
  public void execute(String message) throws JsonProcessingException {

    // if message is empty
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, throwing empty kafka message exception.");
      throw new EmptyKafkaMessageException();
    }

    // Parse the message from debezium
    OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);
    String batchOrderId = receipt.getOrderId();

    // ENSURE THAT EVENT TYPE IS RELATED TO PAYMENT / INVENTORY
    // WILL BE PLACED IN SWITCH CASE TO SET THE STATUS LATER ON
    String eventType = receipt.getEventType();

    logger.info("Notification service received event for order {}: {}", batchOrderId, eventType);

    if (!eventType.equals("NotifyAdmin")) {
      logger.warn("Received wrong type of event for Notification Service. Throwing Exception.");
      throw new WrongKafkaEventException();
    }
    // INPUT BY CLIENT
    List<OrderCreationRequest> requests = receipt.getOrderCreationRequests();
    Map<String, Product> productMap = productRetrievalService.execute(requests);

    Map<String, BigDecimal> purchasesMap = new HashMap<>();

    // LOOP THROUGH USER PAYLOAD
    for (OrderCreationRequest request : requests) {

      // GET THE PRODUCT FORM THE RETRIEVED PRODUCT MAP
      Product product = productMap.get(request.getId());
      // ** GET THE ADMIN ID (CreatedBy)
      String adminId = product.getCreatedBy();

      // GET THE COST OF A SINGLE BATCH
      BigDecimal batchCost = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

      // Update total cost per admin (O(1) operation)
      purchasesMap.merge(adminId, batchCost, BigDecimal::add);
    }

    // USED TO COMPILE ALL NOTIFICATIONS
    // SAVE ALL IN 1 GO AT THE END
    List<Notification> notifications = new ArrayList<>();

    // SHORT NOTIFICATION
    // JUST SHOW THE ADMIN HOW MUCH HE EARNED
    // SAVED IN MYSQL TABLE NOTIFICATION
    for (Map.Entry<String, BigDecimal> entry : purchasesMap.entrySet()) {
      String adminId = entry.getKey();
      BigDecimal totalCost = entry.getValue();

      // CONSTRUCT NOTIFICATION
      // ADD IT INTO NOTIFICATION LIST
      Notification notification =
          new Notification(
              null, adminId, batchOrderId, receipt.getCustomerId(), totalCost, LocalDateTime.now(), "Completed");
      notifications.add(notification);

      logger.info(
          "Notification to admin {}: Customer {} just purchased goods of {}. Payment status -> {}",
          adminId,
          receipt.getCustomerId(),
          totalCost,
          "Completed");
    }

    notificationRepository.saveAll(notifications);
  }
}

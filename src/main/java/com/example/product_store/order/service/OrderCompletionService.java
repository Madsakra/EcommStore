package com.example.product_store.order.service;

import com.example.product_store.notification.service.CreateNotificationService;
import com.example.product_store.order.enums.OrderStatus;
import com.example.product_store.order.events.OrderCompletionEvent;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderCompletionService {

  private final Logger logger = LoggerFactory.getLogger(OrderCompletionService.class);
  private final CreateNotificationService createNotificationService;

  private final OrderRepository orderRepository;

  public OrderCompletionService(
      CreateNotificationService createNotificationService,
      OrderRepository orderRepository) {
    this.createNotificationService = createNotificationService;
    this.orderRepository = orderRepository;
  }

  // SAGA ORCHESTRA WILL RETURN ORDER COMPLETION EVENT
  @KafkaListener(topics = "order-events", groupId = "saga-group")
  public void orderCompletion(OrderCompletionEvent orderCompletionEvent) {
    logger.info(
        "Received Order completion event at OrderService: {} ", orderCompletionEvent);

    Optional<Order> orderOptional =
        orderRepository.findById(orderCompletionEvent.getOrderId());

    if (orderOptional.isPresent()) {
      Order savedOrder = orderOptional.get();

      try {
        // set updated time
        savedOrder.setUpdatedAt(LocalDateTime.now());
        // set the message carried over from events
        savedOrder.setMessage(orderCompletionEvent.getMessage());

        // send notification to admin
        if (orderCompletionEvent.getOrderStatus() == OrderStatus.SUCCESS) {
          createNotificationService.execute(
              orderCompletionEvent.getPurchasesMap(),
              orderCompletionEvent.getOrderId(),
              orderCompletionEvent.getClientId());
          savedOrder.setOrderStatus("Success");
        } else {
          savedOrder.setOrderStatus("Failed");
        }

        orderRepository.save(savedOrder);
        logger.info("Completed Order Information: {}", savedOrder);

      } catch (Exception ex) {
        logger.warn(ex.getMessage());
      }
    }
  }
}

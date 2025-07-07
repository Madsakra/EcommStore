package com.example.product_store.order.service;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.authentication.service.RetrieveAccountService;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.events.OrderCreatedEvent;
import com.example.product_store.order.exceptions.OrderCreationException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.model.OutboxEvent;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderService {


  private final OrdersValidationService ordersValidationService;
  private final OrderRepository orderRepository;
  private final AccountRepository accountRepository;
  private final ObjectMapper objectMapper;
  private final OutboxRepository outboxRepository;
  private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);


  public CreateOrderService(
          KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
          OrdersValidationService ordersValidationService,
          OrderRepository orderRepository, AccountRepository accountRepository, ObjectMapper objectMapper, OutboxRepository outboxRepository) {
    this.ordersValidationService = ordersValidationService;
    this.orderRepository = orderRepository;
    this.accountRepository = accountRepository;
    this.objectMapper = objectMapper;
    this.outboxRepository = outboxRepository;
  }


  @Transactional
  public OrderDTO execute(String jti, List<OrderCreationRequest> orderCreationRequests) {
    try {
      // collector for total costs of products
      BigDecimal tabulated = BigDecimal.ZERO;

      Account account = accountRepository.findById(jti)
              .orElseThrow(() -> new AccountNotFoundException("Unable to find account tied to current user id in jwt: " + jti));

      List<OrderItem> orderItems = new ArrayList<>();

      Order currentOrder = new Order(account);
      logger.info("Current Order before loop in CreateOrderService: {}", currentOrder);

      // Validate and fetch products
      Map<String, Product> productMap = ordersValidationService.execute(orderCreationRequests);

      for (OrderCreationRequest request : orderCreationRequests) {
        Product product = productMap.get(request.getId());
        BigDecimal batchCost = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        tabulated = tabulated.add(batchCost);
        orderItems.add(new OrderItem(product, request.getQuantity(), currentOrder));
      }

      currentOrder.setTotalPrice(tabulated);
      currentOrder.setOrderItems(orderItems);
      currentOrder.setOrderStatus("Processing");
      currentOrder.setMessage("Order received...processing now.");

      // SAVE ORDER INTO DB -> GET ORDER ID
      Order savedOrder = orderRepository.save(currentOrder);

      // Create event
      OrderCreatedEvent event = new OrderCreatedEvent(savedOrder, orderCreationRequests);

      // Insert into outbox
      OutboxEvent outboxEvent = new OutboxEvent(
              null,
              "Order",
              savedOrder.getId(),
              "OrderCreated",
              objectMapper.writeValueAsString(event)
      );
      outboxRepository.save(outboxEvent);

      return new OrderDTO(savedOrder);

    } catch (AccountNotFoundException e) {
      // known error — log and rethrow
      logger.warn("Account not found: {}", e.getMessage());
      throw e;

    } catch (Exception e) {
      // generic error — could be DB failure, JSON failure, etc.
      logger.error("Failed to create order and outbox event due to internal error: {}", e.getMessage());
      throw new OrderCreationException("Failed to create order"+ e.getMessage());
    }
  }


}
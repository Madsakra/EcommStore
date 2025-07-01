package com.example.product_store.order.service;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.service.RetrieveAccountService;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.events.OrderCreatedEvent;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderService {

  private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
  // MAIN SERVICE RUNNING MICROSERVICES
  private final RetrieveAccountService retrieveAccountService;
  private final OrdersValidationService ordersValidationService;
  private final OrderRepository orderRepository;
  private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);

  public CreateOrderService(
      KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
      RetrieveAccountService retrieveAccountService,
      OrdersValidationService ordersValidationService,
      OrderRepository orderRepository) {
    this.kafkaTemplate = kafkaTemplate;
    this.retrieveAccountService = retrieveAccountService;
    this.ordersValidationService = ordersValidationService;
    this.orderRepository = orderRepository;
  }

  public OrderDTO execute(List<OrderCreationRequest> orderCreationRequests) {

    // collector for total costs of products
    BigDecimal tabulated = BigDecimal.ZERO;

    // Retrieve Account ID with Retrieve account microservice
    Account account = retrieveAccountService.execute(null);

    // FOR ADMIN NOTIFICATION SERVICE
    Map<String, BigDecimal> purchasesMap = new HashMap<>();

    // CREATE AN INSTANCE FOR ORDER
    Order currentOrder = new Order(account);
    logger.info("Current Order before loop in CreateOrderService: {}", currentOrder);

    // CREATES AN ARRAY LIST TO STORE ORDER ITEMS LATER
    List<OrderItem> orderItems = new ArrayList<>();

    // GET ALL PRODUCTS FROM DB
    // ALSO CHECK IF USER PAYLOAD IS ACCURATE
    // Once verified products, will place it in a map

    // product id : {Product}
    // easier to retrieve the product later
    Map<String, Product> productMap =
        ordersValidationService.execute(orderCreationRequests);

    // LOOP THROUGH USER PAYLOAD
    for (OrderCreationRequest request : orderCreationRequests) {

      // GET THE PRODUCT FORM THE RETRIEVED PRODUCT MAP
      Product product = productMap.get(request.getId());

      // used in purchases map
      // adminId:{Product}
      String adminId = product.getCreatedBy();

      // Add on the batchCost = product * quantity to tabulated price
      BigDecimal batchCost =
          product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

      // ADD ON TABULATED COSTS
      tabulated = tabulated.add(batchCost);
      orderItems.add(new OrderItem(product, request.getQuantity(), currentOrder));

      // Update total cost per admin (O(1) operation)
      // used for notifications
      purchasesMap.merge(adminId, batchCost, BigDecimal::add);
    }

    // EXIT LOOP, SET TABULATED COSTS TO ORDER
    currentOrder.setTotalPrice(tabulated);

    // save the order item tied to the order -> jpa relationship
    currentOrder.setOrderItems(orderItems);

    // FOR DB, DO NOT USE ENUM for order status, instead the stringified status
    currentOrder.setOrderStatus("Processing");

    // SET NEW ORDER MESSAGE TO PROCESSING
    currentOrder.setMessage("Order received...processing now.");

    // SAVE THE FULL ORDER
    // get the random uuid generated from mysql
    Order savedOrder = orderRepository.save(currentOrder);

    // GENERATE THE EVENT
    // WILL BE CAUGHT BY SAGA ORCHESTRA THROUGH KAFKA LISTENER
    OrderCreatedEvent event =
        new OrderCreatedEvent(
            savedOrder, orderCreationRequests, productMap, purchasesMap);
    kafkaTemplate.send("order-commands", event);

    return new OrderDTO(savedOrder);
  }
}

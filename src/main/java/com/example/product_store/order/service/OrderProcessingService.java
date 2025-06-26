package com.example.product_store.order.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.service.RetrieveAccountService;
import com.example.product_store.order.OrderCreationRequest;

import com.example.product_store.order.dto.NotificationOrderItem;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.repositories.OrderItemRepository;
import com.example.product_store.order.repositories.OrderRepository;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService implements Command<List<OrderCreationRequest>, OrderDTO> {

  // MAIN SERVICE RUNNING MICROSERVICES
  private final RetrieveAccountService retrieveAccountService;
  private final ProductValidationService productValidationService;
  private final StockReductionService stockReductionService;
  private final BalanceReductionService balanceReductionService;
  private final NotificationService notificationService;


  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

  public OrderProcessingService(
          RetrieveAccountService retrieveAccountService,
          ProductValidationService productValidationService,
          StockReductionService stockReductionService,
          BalanceReductionService balanceReductionService, NotificationService notificationService,
          OrderRepository orderRepository,
          OrderItemRepository orderItemRepository) {
    this.stockReductionService = stockReductionService;
    this.retrieveAccountService = retrieveAccountService;
    this.productValidationService = productValidationService;
    this.balanceReductionService = balanceReductionService;
      this.notificationService = notificationService;

      this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  @Override
  @Transactional
  public OrderDTO execute(List<OrderCreationRequest> orderCreationRequests) {

    // collector for total costs of products
    BigDecimal tabulated = BigDecimal.ZERO;

    // Retrieve Account ID with Retrieve account microservice
    Account account = retrieveAccountService.execute(null);

    // CREATE AN INSTANCE FOR ORDER
    Order currentOrder = new Order(account);
    logger.info("Current Order before loop in CreateOrderService: {}", currentOrder);

    // CREATES AN ARRAY LIST TO STORE ORDER ITEMS LATER
    List<OrderItem> orderItems = new ArrayList<>();

    // GET ALL PRODUCTS FROM DB
    // ALSO CHECK IF USER PAYLOAD IS ACCURATE
    Map<String, Product> productMap = productValidationService.execute(orderCreationRequests);



    // Add this outside the loop
    Map<String, BigDecimal> purchasesMap = new HashMap<>();



    // LOOP THROUGH USER PAYLOAD
    for (OrderCreationRequest request : orderCreationRequests) {

      // GET THE PRODUCT FORM THE RETRIEVED PRODUCT MAP
      Product product = productMap.get(request.getId());
      String adminId = product.getCreatedBy();


      // GET THE FULL COSTS
      // DEDUCT FROM USER ACCOUNT IN MICROSERVICE
      BigDecimal batchCost = balanceReductionService.execute(account, product, request);
      // ADD ON TABULATED COSTS
      tabulated = tabulated.add(batchCost);
      // REDUCE STOCK FIRST
      stockReductionService.execute(product, request);


      // Update total cost per admin (O(1) operation)
      purchasesMap.merge(adminId, batchCost, BigDecimal::add);

    }



    // EXIT LOOP, SET TABULATED COSTS TO ORDER
    currentOrder.setTotalPrice(tabulated);
    currentOrder.setOrderItems(orderItems);

    // SAVE THE FULL ORDER AFTER PRICE AND ORDER ITEMS UPDATE
    Order savedOrder = orderRepository.save(currentOrder);
    logger.info("Order saved: {}, in OrderProcessingService", savedOrder);

    notificationService.execute(purchasesMap,savedOrder.getId(),account.getId());

    // SAVE THE ORDER IN MY SQL
    // SEND THE ORDER SUMMARY TO THE CUSTOMER
    return new OrderDTO(savedOrder);
  }
}

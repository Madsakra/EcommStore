package com.example.product_store.order.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.service.RetrieveAccountService;
import com.example.product_store.order.OrderCreationRequest;
import com.example.product_store.order.dto.KafkaOrderGroup;
import com.example.product_store.order.dto.KafkaOrderItem;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.repositories.OrderItemRepository;
import com.example.product_store.order.repositories.OrderRepository;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
  private final KafkaAdminProducerService kafkaAdminProducerService;


  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

  public OrderProcessingService(
          RetrieveAccountService retrieveAccountService,
          ProductValidationService productValidationService,
          StockReductionService stockReductionService,
          BalanceReductionService balanceReductionService, KafkaAdminProducerService kafkaAdminProducerService,
          OrderRepository orderRepository,
          OrderItemRepository orderItemRepository) {
    this.stockReductionService = stockReductionService;
    this.retrieveAccountService = retrieveAccountService;
    this.productValidationService = productValidationService;
    this.balanceReductionService = balanceReductionService;
      this.kafkaAdminProducerService = kafkaAdminProducerService;
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

    // MAP WILL HAVE THE FOLLOWING STRUCTURE:
    // {
    //   adminId:[
    //     productId: xxxx
    //    quantityOrdered: 2
    //    priceAtPurchase: 20.00
    //   ]
    // }
    Map<String, List<KafkaOrderItem>> orderItemMap = new HashMap<>();

    // LOOP THROUGH USER PAYLOAD
    for (OrderCreationRequest request : orderCreationRequests) {

      // GET THE PRODUCT FORM THE RETRIEVED PRODUCT MAP
      Product product = productMap.get(request.getId());
      String adminId = product.getCreatedBy();


      List<KafkaOrderItem> kafkaOrderItems = orderItemMap.computeIfAbsent(adminId, key->new ArrayList<>());

      // GET THE FULL COSTS
      // DEDUCT FROM USER ACCOUNT IN MICROSERVICE
      BigDecimal batchCost = balanceReductionService.execute(account, product, request);
      // ADD ON TABULATED COSTS
      tabulated = tabulated.add(batchCost);
      // REDUCE STOCK FIRST
      stockReductionService.execute(product, request);

      KafkaOrderItem kafkaOrderItem = new KafkaOrderItem(product.getId(),request.getQuantity(),batchCost);
      kafkaOrderItems.add(kafkaOrderItem);

      orderItemMap.put(adminId,kafkaOrderItems);

    }



    // EXIT LOOP, SET TABULATED COSTS TO ORDER
    currentOrder.setTotalPrice(tabulated);
    currentOrder.setOrderItems(orderItems);

    // SAVE THE FULL ORDER AFTER PRICE AND ORDER ITEMS UPDATE
    Order savedOrder = orderRepository.save(currentOrder);
    logger.info("Order saved: {}, in OrderProcessingService", savedOrder);


    // LOOP THROUGH THE KAFKA ORDER ITEM MAP TO SEND NOTIFICATION TO A SINGLE ADMIN
    for (Map.Entry<String,List<KafkaOrderItem>> entry: orderItemMap.entrySet()){
      String adminId = entry.getKey();
      List<KafkaOrderItem> kafkaOrderItems = entry.getValue();



      KafkaOrderGroup kafkaOrderGroup = new KafkaOrderGroup(adminId,savedOrder.getId(),kafkaOrderItems, LocalDateTime.now());

      logger.info("Order Group To be Sent In Order Processing Service: {}",kafkaOrderGroup);
      kafkaAdminProducerService.sendAdminOrderGroup(kafkaOrderGroup);

    }


    // SAVE THE ORDER IN MY SQL
    // SEND THE ORDER SUMMARY TO THE CUSTOMER
    return new OrderDTO(savedOrder);
  }
}

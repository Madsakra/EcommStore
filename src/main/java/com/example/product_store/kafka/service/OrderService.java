package com.example.product_store.kafka.service;


import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.service.RetrieveAccountService;

import com.example.product_store.kafka.events.OrderCreatedEvent;
import com.example.product_store.order.OrderCreationRequest;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.repositories.OrderItemRepository;
import com.example.product_store.order.repositories.OrderRepository;
import com.example.product_store.order.service.*;
import com.example.product_store.store.product.model.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderService {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    // MAIN SERVICE RUNNING MICROSERVICES
    private final RetrieveAccountService retrieveAccountService;
    private final ProductValidationService productValidationService;



    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

    public OrderService(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate, RetrieveAccountService retrieveAccountService,
            ProductValidationService productValidationService,

            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.retrieveAccountService = retrieveAccountService;
        this.productValidationService = productValidationService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }



    public void execute(List<OrderCreationRequest> orderCreationRequests) {

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



        // for sending admin notifications
        // Add this outside the loop
        Map<String, BigDecimal> purchasesMap = new HashMap<>();

        // LOOP THROUGH USER PAYLOAD
        for (OrderCreationRequest request : orderCreationRequests) {


            // GET THE PRODUCT FORM THE RETRIEVED PRODUCT MAP
            Product product = productMap.get(request.getId());
            String adminId = product.getCreatedBy();

            // Add on the product * quantity to tabulated price
            BigDecimal batchCost = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

            // ADD ON TABULATED COSTS
            tabulated = tabulated.add(batchCost);

            // Update total cost per admin (O(1) operation)
            // used for notifications
            purchasesMap.merge(adminId, batchCost, BigDecimal::add);
        }

        // EXIT LOOP, SET TABULATED COSTS TO ORDER
        currentOrder.setTotalPrice(tabulated);
        currentOrder.setOrderItems(orderItems);

        // SAVE THE FULL ORDER
        // get the random uuid generated from mysql
        Order savedOrder = orderRepository.save(currentOrder);
        logger.info("Order saved: {}, in OrderProcessingService", savedOrder);


        // GENERATE THE EVENT
        // WILL BE CAUGHT BY SAGA ORCHESTRA THROUGH KAFKA LISTENER
        OrderCreatedEvent event = new OrderCreatedEvent(savedOrder,orderCreationRequests,productMap);
        kafkaTemplate.send("order-commands",event);
    }
}


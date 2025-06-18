package com.example.product_store.order.service;

import com.example.product_store.Command;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.errors.InsufficientBalanceException;
import com.example.product_store.order.errors.ProductStockException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.OrderCreationRequest;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.repositories.OrderItemRepository;
import com.example.product_store.order.repositories.OrderRepository;
import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.security.model.Account;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderService implements Command<List<OrderCreationRequest>, OrderDTO> {

  private final ProductRepository productRepository;
  private final AccountRepository accountRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);

  public CreateOrderService(
      ProductRepository productRepository,
      AccountRepository accountRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository) {
    this.productRepository = productRepository;
    this.accountRepository = accountRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  @Override
  @Transactional
  public OrderDTO execute(List<OrderCreationRequest> orderCreationRequests) {

      // collector for total costs of products
      BigDecimal tabulated = BigDecimal.ZERO;

      // user id
      String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      logger.info("User id found: {}",userId);

      Account account = accountRepository.findById(userId)
              .orElseThrow(() -> new AccountNotValidException("User not found"));

      // CREATE AN INSTANCE FOR ORDER
      // SET CUSTOMER ID
      // SET CREATED AT
      // SET TOTAL PRICE TO ZERO
      Order currentOrder = new Order();
      currentOrder.setCustomerId(userId);
      currentOrder.setCreatedAt(LocalDateTime.now());
      currentOrder.setTotalPrice(BigDecimal.ZERO);

      logger.info("Current Order before loop: {}",currentOrder);

      // Save order first to get ID
      // UUID GENERATED FROM SQL
      Order savedOrder = orderRepository.save(currentOrder);

      List<OrderItem> orderItems = new ArrayList<>();

      // LOOP THROUGH USER PAYLOAD
      for (OrderCreationRequest request : orderCreationRequests) {
          // CHECK IF THE PRODUCT ID GIVEN BY USER PAYLOAD EXISTS
          Product product = productRepository.findByIdForUpdate(request.getId())
                  .orElseThrow(() -> new ProductNotValidException("Product not found"));

          // CHECK IF THERE IS ENOUGH STOCK
          if (product.getStock() < request.getQuantity()) {
              logger.warn("Not Enough stock for item: {}", product.getTitle());
              throw new ProductStockException("Not enough stock");
          }

          // GET THE FULL COSTS
          BigDecimal fullCost = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
          logger.info("Full Costs of item is : {}",fullCost);

          // CHECK IF THE BALANCE IS SUFFICIENT FOR DEDUCTION
          if (account.getBalance().compareTo(fullCost) < 0) {
              logger.warn("Insufficient balance, left : ${}",account.getBalance());
              throw new InsufficientBalanceException("Insufficient balance to order the product");
          }

          // SUBTRACT BALANCE AND STOCK
          account.setBalance(account.getBalance().subtract(fullCost));
          product.setStock(product.getStock() - request.getQuantity());

          logger.info("Account Balance After Subtraction: {}",account.getBalance());
          logger.info("Product stock after subtraction: {}",product.getStock());

          // CREATE ORDER ITEM OUT OF THE INFO ABOVE
          OrderItem orderItem = new OrderItem();
          orderItem.setOrder(currentOrder);
          orderItem.setProductId(product.getId());
          orderItem.setQuantity(request.getQuantity());
          orderItem.setPriceAtPurchase(fullCost);

          // SAVE ORDER ITEM INTO MYSQL
          orderItemRepository.save(orderItem);

          // ADD ORDER ITEM INTO ORDER LIST
          orderItems.add(orderItem);


          // ADD ON TABULATED COSTS
          tabulated = tabulated.add(fullCost);
      }

      // EXIT LOOP, SET TABULATED COSTS TO ORDER
      savedOrder.setTotalPrice(tabulated);
      savedOrder.setOrderItems(orderItems);

      logger.info("Order after Loop: {}",savedOrder);
      orderRepository.save(savedOrder); // persist the updated totalPrice

      // SAVE THE ORDER IN MY SQL
      return new OrderDTO(savedOrder);
  }

}

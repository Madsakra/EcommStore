package com.example.product_store.order.service;

import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.exceptions.OrderNotFoundException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetOrderService {

  private final OrderRepository orderRepository;

  public static Logger logger = LoggerFactory.getLogger(GetOrderService.class);

  public GetOrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  // PREVENT USER FROM ACCESSING OTHER USERS RESOURCE'S
  // INCLUDE USER ID IN PARAMS
  public OrderDTO execute(String jti, String id) {

    Optional<Order> orderOptional = orderRepository.findById(id);
    if (orderOptional.isPresent()) {
      String customerId = orderOptional.get().getCustomerId();

      if (!customerId.equals(jti)) {
        logger.warn("Unauthorized attempt to access other user's orders blocked out.");
        throw new UnauthorizedManagement();
      }

      Order order = orderOptional.get();
      logger.info("Order: {} has been found", order.getId());
      return new OrderDTO(orderOptional.get());
    }
    logger.warn("Current order {} is not found", id);
    throw new OrderNotFoundException("Order with id: " + id + " cannot be found.");
  }
}

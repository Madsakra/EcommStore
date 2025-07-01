package com.example.product_store.order.events;

import com.example.product_store.order.enums.OrderStatus;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.model.Order;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
  private String orderId;
  private String customerId;
  private BigDecimal totalPrice;
  private LocalDateTime createdAt;
  private OrderStatus orderStatus;
  private List<OrderCreationRequest> orderCreationRequests;
  private Map<String, Product> productMap;
  private Map<String, BigDecimal> purchasesMap;


  public OrderCreatedEvent(
      Order order,
      List<OrderCreationRequest> requests,
      Map<String, Product> productMap,
      Map<String,BigDecimal> purchasesMap

  ) {
    this.orderId = order.getId();
    this.customerId = order.getCustomerId();
    this.totalPrice = order.getTotalPrice();
    this.createdAt = order.getUpdatedAt();
    this.orderStatus = OrderStatus.PROCESSING;
    this.orderCreationRequests = requests;
    this.productMap = productMap;
    this.purchasesMap = purchasesMap;
  }
}

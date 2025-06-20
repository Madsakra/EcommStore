package com.example.product_store.order.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaOrderItem {
  private String productId;
  private Integer quantityOrdered;
  private BigDecimal priceAtPurchase;
}

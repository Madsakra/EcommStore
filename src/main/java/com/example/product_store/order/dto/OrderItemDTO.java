package com.example.product_store.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private String orderItemId;
    private String productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}

package com.example.product_store.order.dto;

import com.example.product_store.order.model.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderDTO implements Serializable {
    private String id;
    private String customerId;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.customerId = order.getCustomerId();
        this.totalPrice = order.getTotalPrice();
        this.createdAt = order.getCreatedAt();
    }
}

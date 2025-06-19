package com.example.product_store.order.model;

import com.example.product_store.order.dto.OrderItemDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String customerId;
    private String adminId;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
}

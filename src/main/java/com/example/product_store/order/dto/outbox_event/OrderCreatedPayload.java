package com.example.product_store.order.dto.outbox_event;

import com.example.product_store.order.dto.OrderCreationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedPayload {
    private String orderId;
    private String createdAt;
    private String customerId;
    private BigDecimal totalPrice;
    private String orderStatus;
    private List<OrderCreationRequest> orderCreationRequests;
}

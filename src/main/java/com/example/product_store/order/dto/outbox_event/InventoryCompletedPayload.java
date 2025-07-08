package com.example.product_store.order.dto.outbox_event;

import com.example.product_store.order.dto.OrderCreationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCompletedPayload {
    private String orderId;
    private String status;
    private List<OrderCreationRequest> orderCreationRequests;
}

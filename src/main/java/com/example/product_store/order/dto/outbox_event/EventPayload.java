package com.example.product_store.order.dto.outbox_event;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPayload {

    // USED WHEN SYSTEM IS TRYING TO EMIT EVENT
    // Payload column in Outbox_event table
    private String orderId;
    private String customerId;
    private BigDecimal totalPrice;
    private List<OrderCreationRequest> orderCreationRequests;


    // WHEN ORDERS ARE FRESHLY CREATED
    public EventPayload(
            Order order,
            List<OrderCreationRequest> requests
    ) {
        this.orderId = order.getId();
        this.customerId = order.getCustomerId();
        this.totalPrice = order.getTotalPrice();
        this.orderCreationRequests = requests;
    }


    public EventPayload(OutboxEventReceipt outboxEventReceipt)
    {
        this.orderId = outboxEventReceipt.getOrderId();
        this.customerId = outboxEventReceipt.getCustomerId();
        this.totalPrice = outboxEventReceipt.getTotalPrice();
        this.orderCreationRequests = outboxEventReceipt.getOrderCreationRequests();
    }





}

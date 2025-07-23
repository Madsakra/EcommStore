package com.example.product_store.order.dto.outbox_event;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


// FOR KAFKA CONNECT / DEBEZIUM TO RECEIVE AND PARSE
// PAYLOAD COLUMN IN OUTBOX TABLE (Except eventType)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEventReceipt {
    private String orderId;
    private String customerId;
    private BigDecimal totalPrice;
    private List<OrderCreationRequest> orderCreationRequests;
    @JsonProperty("eventType")
    private String eventType;
}

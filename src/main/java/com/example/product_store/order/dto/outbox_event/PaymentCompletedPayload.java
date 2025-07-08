package com.example.product_store.order.dto.outbox_event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletedPayload {
    private String orderId;
    private String status;
    private BigDecimal totalPrice;
}

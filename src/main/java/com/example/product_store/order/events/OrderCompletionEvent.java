package com.example.product_store.order.events;

import com.example.product_store.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCompletionEvent {
    private String orderId;
    private String clientId;
    private OrderStatus orderStatus;
    private String message;
    private Map<String, BigDecimal> purchasesMap;
}

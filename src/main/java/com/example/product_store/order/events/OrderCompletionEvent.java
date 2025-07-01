package com.example.product_store.order.events;

import com.example.product_store.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCompletionEvent {
    private String orderId;
    private OrderStatus orderStatus;
    private String message;
}

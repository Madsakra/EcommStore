package com.example.product_store.order.kafka.events;

import lombok.Data;

@Data
public class OrderEvent {

    private String orderId;
    private String status;

    // Constructor, Getters, and Setters
}

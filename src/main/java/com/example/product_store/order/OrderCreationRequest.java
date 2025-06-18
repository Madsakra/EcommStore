package com.example.product_store.order;

import lombok.Data;

@Data
public class OrderCreationRequest {
    // ID that user will input
    private String id;
    // quantity that user will input
    private Integer quantity;
}

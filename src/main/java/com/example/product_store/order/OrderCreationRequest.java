package com.example.product_store.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationRequest {
    // ID that user will input
    private String id;
    // quantity that user will input
    private Integer quantity;
}

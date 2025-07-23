package com.example.product_store.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationRequest {
    // SINGLE PRODUCT FROM CLIENT REQUEST
    // WILL BE USED IN A LIST
    // ID that user will input
    private String id;
    // quantity that user will input
    private Integer quantity;
}

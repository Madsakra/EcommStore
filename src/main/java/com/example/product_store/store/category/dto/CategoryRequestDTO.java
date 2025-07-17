package com.example.product_store.store.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CategoryRequestDTO {
    // PAYLOAD BY USER
    // CAN INCLUDE MORE IN FUTURE
    private String name;
}

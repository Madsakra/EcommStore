package com.example.product_store.store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    // CLASS TO HOLD FILTER FROM USER
    String title;
    String description;
    List<String> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}

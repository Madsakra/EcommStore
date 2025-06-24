package com.example.product_store.store.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    String title;
    String description;
    List<String> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;

}

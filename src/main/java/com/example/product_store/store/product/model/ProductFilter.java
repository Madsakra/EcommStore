package com.example.product_store.store.product.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductFilter {
    List<String> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;

}

package com.example.product_store.store.product.util;

import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.ProductFilter;


import java.math.BigDecimal;

public class ProductFilterValidator {

    // CHECK IF THE FILTERS GIVEN BY CLIENT IS LEGIT
    public static void execute(ProductFilter productFilter){
        if (productFilter.getMinPrice() != null) {
            if (productFilter.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidPageRequestException("Min price cannot be negative.");
            }

        }

        if (productFilter.getMaxPrice() != null) {
            if (productFilter.getMaxPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidPageRequestException("Max price cannot be negative.");
            }

        }

        if (productFilter.getMinPrice() != null && productFilter.getMaxPrice() != null &&
                productFilter.getMinPrice().compareTo(productFilter.getMaxPrice()) > 0) {
            throw new InvalidPageRequestException("Min price cannot be greater than max price.");
        }
    }
}

package com.example.product_store.order;

import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;

import java.util.List;
import java.util.Map;


public class Utilities {

    // CHECK IF THE PRODUCT IDS GIVEN BY CLIENT IS VALID
    // TO INCLUDE THE LIST OF PRODUCTS IDS,
    // AND PRODUCT MAP FOR FASTER FETCHING
    public static void productChecker(List<String> productIds, Map<String, Product> productMap){
        // 4. Check if any product ID is missing
        List<String> missingProductIds = productIds.stream()
                .filter(id -> !productMap.containsKey(id))
                .toList();

        if (!missingProductIds.isEmpty()) {
            throw new ProductNotFoundException("Some products were not found: " + missingProductIds);
        }
    }

}

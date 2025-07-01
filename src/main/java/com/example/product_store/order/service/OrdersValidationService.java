package com.example.product_store.order.service;

import com.example.product_store.Command;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdersValidationService implements Command<List<OrderCreationRequest>, Map<String,Product>> {

    private final ProductRepository productRepository;

    public OrdersValidationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    // TLDR
    // FETCH FROM DB FIRST
    // BUILD A MAP OUT OF THE PRODUCTS AND ITS ID
    // RUN THE CHECKER THROUGH THE MAP TO CHECK IF PRODUCTS ARE MISSING

    @Override
    public Map<String, Product> execute(List<OrderCreationRequest> orderCreationRequests) {
        // --------- FINDING PRODUCTS---------------//
        // GO THROUGH THE USER INPUT, EXTRACT ID INTO LIST OF STRINGS
        List<String> productIDs = orderCreationRequests.stream().map(OrderCreationRequest::getId).toList();

        // FIND ALL THE PRODUCT IDS IN REPOSITORY
        List<Product> products = productRepository.findAllByIdForUpdate(productIDs);

        // CREATE A MAP {ID: PRODUCT OBJECT}
        // don't have to find in repo later, reduces n(N) query in DB to n(1)
        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, p -> p));

        // CHECK WHETHER THE PRODUCTS GIVEN BY CLIENT IS VALID
        // If any product fail the check, error will be thrown and the whole order operation will be stopped
        productChecker(productIDs, productMap);

        // will return product map if product checker passes through checks.
        return productMap;

    }


    private void productChecker(List<String> productIds, Map<String, Product> productMap){
        // 4. Check if any product ID is missing
        List<String> missingProductIds = productIds.stream()
                .filter(id -> !productMap.containsKey(id))
                .toList();

        if (!missingProductIds.isEmpty()) {
            throw new ProductNotFoundException("Some products were not found: " + missingProductIds);
        }
    }

}

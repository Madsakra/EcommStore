package com.example.product_store.order.service;

import com.example.product_store.Command;
import com.example.product_store.order.OrderCreationRequest;
import com.example.product_store.order.util.Utilities;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductValidationService implements Command<List<OrderCreationRequest>, Map<String,Product>> {

    private final ProductRepository productRepository;

    public ProductValidationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


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
        Utilities.productChecker(productIDs, productMap);

        // will return product map if product checker passes through checks.
        return productMap;

    }







}

package com.example.product_store.order.service;

import com.example.product_store.Command;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductRetrievalService implements Command<List<OrderCreationRequest>, Map<String, Product>> {

  // SPECIALLY FOR THE NOTIFICATION SERVICE
  // ********  NOT FOR THE CONTROLLER END POINT RELATED TO PRODUCT *******
  private final ProductRepository productRepository;

  public ProductRetrievalService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Map<String, Product> execute(List<OrderCreationRequest> requests) {

    // --------- FINDING PRODUCTS---------------//
    // GO THROUGH THE USER INPUT, EXTRACT ID INTO LIST OF STRINGS
    List<String> productIDs = requests.stream().map(OrderCreationRequest::getId).toList();

    // FIND ALL PRODUCTS IN REPOSITORY
    List<Product> products = productRepository.findAllByIdForUpdate(productIDs);
    // CREATE A MAP {ID: PRODUCT OBJECT}
    // don't have to find in repo later, reduces n(N) query in DB to n(1)
    Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, p -> p));
    productChecker(productIDs, productMap);
    return productMap;
  }

  // CHECK IF THE PRODUCT IDS GIVEN BY CLIENT IS VALID
  // TO INCLUDE THE LIST OF PRODUCTS IDS,
  // AND PRODUCT MAP FOR FASTER FETCHING
  public void productChecker(List<String> productIds, Map<String, Product> productMap) {
    // 4. Check if any product ID is missing
    List<String> missingProductIds = productIds.stream().filter(id -> !productMap.containsKey(id)).toList();

    if (!missingProductIds.isEmpty()) {
      throw new ProductNotFoundException("Some products were not found: " + missingProductIds);
    }
  }
}

package com.example.product_store.order.service.processing;

import com.example.product_store.CacheConstants;
import com.example.product_store.order.events.InventoryCompletedEvent;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RollBackInventory {
  private final ProductRepository productRepository;

  public RollBackInventory(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, allEntries = true)
      },
      put = {
        @CachePut(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = "'allProducts'")
      })
  public boolean execute(InventoryCompletedEvent inventoryCompletedEvent) {

    // will need to tap into db again to activate pessimistic lock
    List<String> ids =
        inventoryCompletedEvent.getRequests().stream()
            .map(OrderCreationRequest::getId)
            .toList();
    List<Product> lockedProducts = productRepository.findAllById(ids);
    // Map locked products by ID for fast lookup
    Map<String, Product> lockedProductMap =
        lockedProducts.stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

    for (OrderCreationRequest request : inventoryCompletedEvent.getRequests()) {
      Product product = lockedProductMap.get(request.getId());
      if (product == null) {
        throw new ProductStockException("Product not found: " + request.getId());
      }
      product.setStock(product.getStock() + request.getQuantity());
    }

    productRepository.saveAll(lockedProducts);
    return true;
  }
}

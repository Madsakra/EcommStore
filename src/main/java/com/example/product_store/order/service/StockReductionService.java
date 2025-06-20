package com.example.product_store.order.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.order.OrderCreationRequest;
import com.example.product_store.order.errors.ProductStockException;
import com.example.product_store.store.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class StockReductionService {

    // SERVICE FOR REDUCING STOCK IN ORDER PROCESSING
  public static Logger logger = LoggerFactory.getLogger(StockReductionService.class);

  @Caching(
          evict = {@CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, allEntries = true)},
          put = {@CachePut(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = "'allProducts'")})
  public Void execute(Product product, OrderCreationRequest request) {
    logger.info("Product stock for {} before subtraction in StockReductionService: {}",product.getTitle(),product.getStock());
    // CHECK IF THERE IS ENOUGH STOCK
    if (product.getStock() < request.getQuantity()) {
      logger.warn("Not Enough stock for item: {}, rolling back", product.getTitle());
      throw new ProductStockException("Not enough stock");
    }

    // ONCE THE STOCK PASSES CHECK, REDUCE IT IN DB
    // THEN RETURN NULL
    product.setStock(product.getStock() - request.getQuantity());
    logger.info("Product stock for {} after subtraction in StockReductionService, {}", product.getTitle() ,product.getStock());
    return null;
  }
}

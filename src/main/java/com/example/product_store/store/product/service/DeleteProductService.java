package com.example.product_store.store.product.service;

import com.example.product_store.Command;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class DeleteProductService implements Command<String, Void> {

  private final ProductRepository productRepository;

  public DeleteProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(cacheNames = "product", allEntries = true),
        @CacheEvict(cacheNames = "getAllProducts", key = "'allProducts'")
      })
  public Void execute(String id) {
    Optional<Product> productOptional = productRepository.findById(id);
    if (productOptional.isPresent()) {
      productRepository.deleteById(id);
      return null;
    }

    throw new ProductNotFoundException();
  }
}

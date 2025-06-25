package com.example.product_store.store.product.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.Command;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
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
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, allEntries = true)
      })
  public Void execute(String id) {
    Optional<Product> productOptional = productRepository.findById(id);
    if (productOptional.isPresent()) {

      Product dbProduct = productOptional.get(); // This is the actual DB object

      // 2. Check if the product belongs to the user
      // get hold of the current user UUID THROUGH THE JWT, via context provider
      String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();



      // 3. if product does not belong to user, throw error
      if (!dbProduct.getCreatedBy().matches(jti)){
        throw new UnauthorizedManagement("This product does not belongs to you!");
      }


      productRepository.deleteById(id);
      return null;
    }

    throw new ProductNotFoundException("Product does not exist based on id!");
  }
}

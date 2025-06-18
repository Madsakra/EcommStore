package com.example.product_store.store.product.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.Command;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.UpdateProductCommand;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.dto.ProductDTO;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductService implements Command<UpdateProductCommand, ProductDTO> {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;

  public UpdateProductService(ProductRepository productRepository, ProductValidator productValidator) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
  }

  @Override
  @Caching(
      evict = {@CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, allEntries = true)},
        put = {@CachePut(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = "'allProducts'")})
  public ProductDTO execute(UpdateProductCommand command) {


    // 1. Find the item in db first
    Optional<Product> productOptional = productRepository.findById(command.getId());
    if (productOptional.isPresent()) {

      Product dbProduct = productOptional.get(); // This is the actual DB object

      // 2. Check if the product belongs to the user
      // get hold of the current user UUID THROUGH THE JWT, via context provider
      String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


      Product product = command.getProduct();

      // 3. if product does not belong to user, throw error
      if (!dbProduct.getCreatedBy().matches(jti)){
          throw new UnauthorizedManagement("This product does not belongs to you!");
      }

      product.setId(command.getId());
      product.setCreatedBy(jti);
      productValidator.execute(product, true);
      productRepository.save(product);
      return new ProductDTO(product);
    }

    throw new ProductNotFoundException();
  }
}

package com.example.product_store.store.product.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.UpdateProductCommand;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductService {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;

  public UpdateProductService(
      ProductRepository productRepository, ProductValidator productValidator) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
  }

  @Caching(
      evict = {
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, allEntries = true)
      },
      put = {
        @CachePut(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = "'allProducts'")
      })
  public ProductDTO execute(String jti, UpdateProductCommand command) {

    // 1. Find the item in db first
    Optional<Product> productOptional = productRepository.findById(command.getId());
    if (productOptional.isPresent()) {

      Product dbProduct = productOptional.get(); // This is the actual DB object
      // 2. if product does not belong to admin, throw error
      if (!dbProduct.getCreatedBy().matches(jti)) {
        throw new UnauthorizedManagement("This product does not belongs to you!");
      }

      ProductRequestDTO requestDTO = command.getRequestDTO();
      requestDTO.setCreatedBy(jti);
      productValidator.execute(requestDTO, true);
      Product product = new Product(requestDTO);
      // 1. when creating product, id is null
      // 2. set the id of the product in db to new product
      product.setId(command.getId());
      productRepository.save(product);
      return new ProductDTO(product);
    }

    throw new ProductNotFoundException("Product does not exist based on id!");
  }
}

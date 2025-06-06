package com.example.product_store.store.product.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.Command;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.UpdateProductCommand;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductDTO;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
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
      evict = {@CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = CacheConstants.ALL_PRODUCTS_KEY)},
      put = {@CachePut(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = CacheConstants.ALL_PRODUCTS_KEY)})
  public ProductDTO execute(UpdateProductCommand command) {
    // 1. Find the item in db first
    Optional<Product> productOptional = productRepository.findById(command.getId());
    if (productOptional.isPresent()) {
      Product product = command.getProduct();
      product.setId(command.getId());
      productValidator.execute(product, true);
      productRepository.save(product);
      return new ProductDTO(product);
    }

    throw new ProductNotFoundException();
  }
}

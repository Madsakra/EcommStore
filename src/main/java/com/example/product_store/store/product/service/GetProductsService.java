package com.example.product_store.store.product.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.specification.ProductSpecification;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

// ONLY CLASS THAT DON'T USE QUERY BINDER
@Service
public class GetProductsService {

  private final ProductRepository productRepository;

  public GetProductsService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  // ORDINARY SEARCH
  // TO BE REPLACED BY ELASTIC SEARCH ONCE WORKING
  @Cacheable(cacheNames = "getAllProducts", keyGenerator = "productFilterKeyGenerator")
  // FILTER SEARCH, TO REPLACE ABOVE LATER
  public List<ProductDTO> execute(ProductFilter productFilter, Pageable pageable) {
    // USE SPECIFICATIONS FOR FILTER
    Specification<Product> spec = ((root, query, criteriaBuilder) -> null);

    // CHECK: MINIMUM PRICE CANNOT BE NEGATIVE
    if (productFilter.getMinPrice() != null && productFilter.getMinPrice().compareTo(BigDecimal.valueOf(0))<=0) {
      throw new InvalidPageRequestException("Min price cannot be negative.");
    }
    // CHECK: MAX PRICE CANNOT BE MAX
    if (productFilter.getMaxPrice() != null && productFilter.getMaxPrice().compareTo(BigDecimal.valueOf(0))<=0) {
      throw new InvalidPageRequestException("Max price cannot be negative.");
    }

    // CHECK: MIN PRICE CANNOT BE > THAN MAX
    if (productFilter.getMaxPrice() != null
        && productFilter.getMinPrice() != null
        && productFilter.getMinPrice().compareTo(productFilter.getMaxPrice()) > 0) {
      throw new InvalidPageRequestException("Min price cannot be greater than max price.");
    }

    // FILTER BY CATEGORY FIRST (IF HAVE)
    if (productFilter.getCategoryIds() != null && !productFilter.getCategoryIds().isEmpty()) {
      for (String id : productFilter.getCategoryIds()) {
        spec = spec.and(ProductSpecification.hasCategoryId(id));
      }
    }

    // FILTER BY PRICE
    if (productFilter.getMinPrice() != null) {
      spec = spec.and(ProductSpecification.hasPriceGreaterThan(productFilter.getMinPrice()));
    }

    if (productFilter.getMaxPrice() != null) {
      spec = spec.and(ProductSpecification.hasPriceLessThan(productFilter.getMaxPrice()));
    }

    Page<Product> products = productRepository.findAll(spec,pageable);
    List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
    return productDTOS;
  }







}

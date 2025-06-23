package com.example.product_store.store.product.service;

import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.specification.ProductSpecification;
import java.math.BigDecimal;
import java.util.List;

import com.example.product_store.store.product.util.ProductFilterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

// NOT USING QUERY BINDER DUE TO DIFF PARAMS
@Service
public class GetProductsService {

  private final ProductRepository productRepository;
  public static final Logger logger = LoggerFactory.getLogger(GetProductsService.class);

  public GetProductsService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Cacheable(cacheNames = "getAllProducts", keyGenerator = "productFilterKeyGenerator")
  public List<ProductDTO> execute(ProductFilter productFilter, Pageable pageable) {

    // USE SPECIFICATIONS FOR FILTER
    Specification<Product> spec = ((root, query, criteriaBuilder) -> null);
    logger.info("Given Payload by client : {}", productFilter);

    // IF ENCOUNTER INVALID PAYLOAD
    // WILL JUST THROW ERROR FROM HERE AND REFUSE TO CONTINUE
    ProductFilterValidator.execute(productFilter);



    if (productFilter.getMinPrice() != null) {
      if (productFilter.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
        throw new InvalidPageRequestException("Min price cannot be negative.");
      }
      spec = spec.and(ProductSpecification.hasPriceGreaterThan(productFilter.getMinPrice()));
    }

    if (productFilter.getMaxPrice() != null) {
      if (productFilter.getMaxPrice().compareTo(BigDecimal.ZERO) < 0) {
        throw new InvalidPageRequestException("Max price cannot be negative.");
      }
      spec = spec.and(ProductSpecification.hasPriceLessThan(productFilter.getMaxPrice()));
    }

    if (productFilter.getMinPrice() != null && productFilter.getMaxPrice() != null &&
            productFilter.getMinPrice().compareTo(productFilter.getMaxPrice()) > 0) {
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

    Page<Product> products = productRepository.findAll(spec, pageable);
    List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
    logger.info("Returned ProductDTOS: {}", productDTOS);


    return productDTOS;
  }
}

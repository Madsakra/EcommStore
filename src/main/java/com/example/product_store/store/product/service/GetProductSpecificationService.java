package com.example.product_store.store.product.service;

import com.example.product_store.Command;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.specification.ProductSpecification;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class GetProductSpecificationService
    implements Command<ProductFilter, Specification<Product>> {

  public static final Logger logger =
      LoggerFactory.getLogger(GetProductSpecificationService.class);

  @Override
  public Specification<Product> execute(ProductFilter productFilter) {

    // USE SPECIFICATIONS FOR FILTER
    Specification<Product> spec = ((root, query, criteriaBuilder) -> null);
    logger.info("ProductFilterService: Product Filter fields are: {}", productFilter);

    // IF ENCOUNTER INVALID PAYLOAD
    // WILL JUST THROW ERROR FROM HERE AND REFUSE TO CONTINUE
    if (productFilter.getMinPrice() != null) {
      if (productFilter.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
        throw new InvalidPageRequestException(
            "ProductFilterService: Min price cannot be negative.");
      }
      spec =
          spec.and(ProductSpecification.hasPriceGreaterThan(productFilter.getMinPrice()));
    }

    if (productFilter.getMaxPrice() != null) {
      if (productFilter.getMaxPrice().compareTo(BigDecimal.ZERO) < 0) {
        throw new InvalidPageRequestException(
            "ProductFilterService: Max price cannot be negative.");
      }
      spec = spec.and(ProductSpecification.hasPriceLessThan(productFilter.getMaxPrice()));
    }

    if (productFilter.getMinPrice() != null
        && productFilter.getMaxPrice() != null
        && productFilter.getMinPrice().compareTo(productFilter.getMaxPrice()) > 0) {
      throw new InvalidPageRequestException(
          "ProductFilterService: Min price cannot be greater than max price.");
    }

    // FILTER BY CATEGORY FIRST (IF HAVE)
    if (productFilter.getCategoryIds() != null
        && !productFilter.getCategoryIds().isEmpty()) {
      for (String id : productFilter.getCategoryIds()) {
        spec = spec.and(ProductSpecification.hasCategoryId(id));
      }
    }

    // FILTER BY PRICE
    if (productFilter.getMinPrice() != null) {
      spec =
          spec.and(ProductSpecification.hasPriceGreaterThan(productFilter.getMinPrice()));
    }

    if (productFilter.getMaxPrice() != null) {
      spec = spec.and(ProductSpecification.hasPriceLessThan(productFilter.getMaxPrice()));
    }

    return spec;
  }
}

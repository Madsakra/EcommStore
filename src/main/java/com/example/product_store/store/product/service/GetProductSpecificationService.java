package com.example.product_store.store.product.service;

import com.example.product_store.Command;
import com.example.product_store.store.product.dto.ProductFilter;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.specification.ProductSpecification;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class GetProductSpecificationService implements Command<ProductFilter, Specification<Product>> {

  public static final Logger logger = LoggerFactory.getLogger(GetProductSpecificationService.class);

  @Override
  public Specification<Product> execute(ProductFilter productFilter) {

    // USE SPECIFICATIONS FOR FILTER
    Specification<Product> spec = ((root, query, criteriaBuilder) -> null);
    logger.info("ProductFilterService: Product Filter fields are: {}", productFilter);

    // MINIMUM PRICE < 0 OR NULL
    // THROW INVALID PAGE REQUEST EXCEPTION
    if (productFilter.getMinPrice() != null) {
      if (productFilter.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
        logger.warn(
            "GetProductSpecificationService: Payload given by client consists of negative"
                + " minimum price for product.");
        throw new InvalidPageRequestException("Please check your headers: Minimum price is negative");
      }
      spec = spec.and(ProductSpecification.hasPriceGreaterThan(productFilter.getMinPrice()));
    }

    // MAX PRICE < 0 OR NULL
    // THROW INVALID PAGE REQUEST EXCEPTION
    if (productFilter.getMaxPrice() != null) {
      if (productFilter.getMaxPrice().compareTo(BigDecimal.ZERO) < 0) {
        logger.warn(
            "GetProductSpecificationService: Payload given by client consists of negative"
                + " maximum price for product.");
        throw new InvalidPageRequestException("Please check your headers: Maximum price is negative");
      }
      spec = spec.and(ProductSpecification.hasPriceLessThan(productFilter.getMaxPrice()));
    }

    // IF MINIMUM PRICE > MAX PRICE
    if (productFilter.getMinPrice() != null
        && productFilter.getMaxPrice() != null
        && productFilter.getMinPrice().compareTo(productFilter.getMaxPrice()) > 0) {
      logger.warn("GetProductSpecificationService: Payload given by client has min price > max" + " price of product.");
      throw new InvalidPageRequestException("Please check your headers: min price > max price of product");
    }

    // FILTER BY CATEGORY (IF PROVIDED BY USER)
    // WRONG / INVALID CATEGORY ID WILL RETURN EMPTY PAGE
    if (productFilter.getCategoryIds() != null && !productFilter.getCategoryIds().isEmpty()) {
      spec = spec.and(ProductSpecification.hasCategoryIds(productFilter.getCategoryIds()));
    }

    return spec;
  }
}

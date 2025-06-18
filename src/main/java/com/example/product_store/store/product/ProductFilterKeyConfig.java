package com.example.product_store.store.product;

import com.example.product_store.store.product.model.ProductFilter;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
public class ProductFilterKeyConfig {

  @Bean("productFilterKeyGenerator")
  public KeyGenerator productFilterKeyGenerator() {
    return (target, method, params) -> {
      ProductFilter filter = (ProductFilter) params[0];
      Pageable pageable = (Pageable) params[1];
      return String.format(
          "min=%s|max=%s|cats=%s|page=%d|size=%d|sort=%s",
          filter.getMinPrice(),
          filter.getMaxPrice(),
          filter.getCategoryIds(),
          pageable.getPageNumber(),
          pageable.getPageSize(),
          pageable.getSort());
    };
  }
}

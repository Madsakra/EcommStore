package com.example.product_store.store.product.service;

import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import java.util.List;
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
  private final GetProductSpecificationService getProductSpecificationService;
  public static final Logger logger = LoggerFactory.getLogger(GetProductsService.class);

  public GetProductsService(
      ProductRepository productRepository,
      GetProductSpecificationService getProductSpecificationService) {
    this.productRepository = productRepository;
    this.getProductSpecificationService = getProductSpecificationService;
  }

  @Cacheable(cacheNames = "getAllProducts", keyGenerator = "productFilterKeyGenerator")
  public List<ProductDTO> execute(ProductFilter productFilter, Pageable pageable) {

    // CONVERT THE PRODUCT FILTER (PAYLOAD FROM CLIENT) TO PRODUCT SPECIFICATION FOR JPA
    Specification<Product> spec = getProductSpecificationService.execute(productFilter);

    Page<Product> products = productRepository.findAll(spec, pageable);
    List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
    logger.info("Returned ProductDTOS: {}", productDTOS);

    return productDTOS;
  }
}

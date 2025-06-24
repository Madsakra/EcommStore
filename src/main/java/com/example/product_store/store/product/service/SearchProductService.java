package com.example.product_store.store.product.service;

import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.specification.ProductSpecification;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SearchProductService {

  private final ProductRepository productRepository;
  public final Logger logger = LoggerFactory.getLogger(SearchProductService.class);
  private final GetProductSpecificationService getProductSpecificationService;

  public SearchProductService(
      ProductRepository productRepository, GetProductSpecificationService getProductSpecificationService) {
    this.productRepository = productRepository;
    this.getProductSpecificationService = getProductSpecificationService;
  }

  // DEFAULT SEARCH BY TITLE
  // NOT CACHING BECAUSE OF FILTER
  // TITLE SEARCHES CAN ALSO BE DYNAMIC
  public List<ProductDTO> execute( ProductFilter productFilter, Pageable pageable) {

    // THROW ERROR IF TITLE IS EMPTY OR NULL IN PAYLOAD FROM CLIENT
    if (productFilter.getTitle() == null || productFilter.getTitle().isBlank()) {
      logger.warn("Title is empty / null in client's payload");
      throw new InvalidPageRequestException("Title cannot be null or empty!");
    }
    Specification<Product> spec = getProductSpecificationService.execute(productFilter);
    spec = spec.and(ProductSpecification.titleContains(productFilter.getTitle()));

    if (productFilter.getDescription()!=null && !productFilter.getDescription().isBlank()){
      spec = spec.and(ProductSpecification.descriptionContaining(productFilter.getDescription()));
    }

    // use repository to find all products with the filter
    Page<Product> products = productRepository.findAll(spec, pageable);
    List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
    logger.info("Returned ProductDTOS in SearchProductService: {}", productDTOS);
    return productDTOS;
  }


}

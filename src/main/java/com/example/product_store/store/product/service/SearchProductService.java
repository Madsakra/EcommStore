package com.example.product_store.store.product.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchProductService implements QueryBinder<String, List<ProductDTO>> {

  private final ProductRepository productRepository;

  public SearchProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  // DEFAULT SEARCH BY TITLE
  // METHOD SIGNATURE -> execute
  @Override
  //    @Cacheable(cacheNames = "product", key = "#title")
  public List<ProductDTO> execute(String title) {
    return productRepository.findByTitleContaining(title).stream().map(ProductDTO::new).toList();
  }

  // SEARCH BY DESCRIPTION
  //    @Cacheable(cacheNames = "product", key = "#description")
  public List<ProductDTO> searchProductByDescription(String description) {
    return productRepository.findByDescriptionContaining(description).stream().map(ProductDTO::new).toList();
  }
}

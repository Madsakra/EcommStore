package com.example.product_store.store.product.service;

import com.example.product_store.Query;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.dto.ProductDTO;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetProductsService implements Query<Void, List<ProductDTO>> {

  private final ProductRepository productRepository;

  public GetProductsService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  @Cacheable(cacheNames = "getAllProducts", key = "'allProducts'")
  public List<ProductDTO> execute(Void input) {
    List<Product> products = productRepository.findAll();
    List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();

    return productDTOS;
  }
}

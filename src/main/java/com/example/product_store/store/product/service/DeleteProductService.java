package com.example.product_store.store.product.service;

import com.example.product_store.store.product.repositories.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DeleteProductService {

  private final ProductRepository productRepository;

  public DeleteProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Void execute(String jti, String id) {

    // FIND THE PRODUCT BY ID IN DB FIRST
    Optional<Product> productOptional = productRepository.findById(id);
    if (productOptional.isPresent()) {
      // IF PRODUCT IS PRESENT
      Product dbProduct = productOptional.get();
      // if product does not belong to admin, throw error
      if (!dbProduct.getCreatedBy().matches(jti)) {
        throw new UnauthorizedManagement();
      }

      // otherwise delete and return no content
      productRepository.deleteById(id);
      return null;
    }
    throw new ProductNotFoundException();
  }
}

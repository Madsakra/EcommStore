package com.example.product_store.store.product.service;

import com.example.product_store.store.product.repositories.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.UpdateProductCommand;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UpdateProductService {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;

  public UpdateProductService(
      ProductRepository productRepository, ProductValidator productValidator) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
  }

  public ProductDTO execute(String jti, UpdateProductCommand command) {

    // Find the item in db first
    Optional<Product> productOptional = productRepository.findById(command.getId());
    if (productOptional.isPresent()) {

      // 1. This is the actual DB object
      Product dbProduct = productOptional.get();
      // 2. if product does not belong to admin, throw error
      if (!dbProduct.getCreatedBy().matches(jti)) {
        throw new UnauthorizedManagement();
      }

      ProductRequestDTO requestDTO = command.getRequestDTO();
      requestDTO.setCreatedBy(jti);

      // 3. VALIDATE THE PRODUCT, ANY ERRORS WILL RESULT IN NULL
      productValidator.execute(requestDTO);

      // 4. CREATE A NEW PRODUCT INSTANCE (WITHOUT THE ID)
      Product product = new Product(requestDTO);

      // 5. SET THE ID (DB OBJECT) TO THE ABOVE INSTANCE
      product.setId(command.getId());
      Product savedProduct = productRepository.save(product);
      return new ProductDTO(savedProduct);
    }

    throw new ProductNotFoundException();
  }
}

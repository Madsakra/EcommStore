package com.example.product_store.store.product.service;

import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateProductService {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;
  public static final Logger logger = LoggerFactory.getLogger(CreateProductService.class);

  public CreateProductService(
      ProductRepository productRepository, ProductValidator productValidator) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
  }

  public ProductDTO execute(String jti, ProductRequestDTO requestDTO) {

    // set createdBy to current User
    requestDTO.setCreatedBy(jti);

    logger.info(
        "CreateProductService: The product information before saving into DB: {}",
        requestDTO);

    // VALIDATE THE PRODUCT
    productValidator.execute(requestDTO, false);
    // IF NO ERRORS, SAVE

    Product product = new Product(requestDTO);
    Product savedProduct = productRepository.save(product);
    logger.info(
        "CreateProductService: The product information after saving into DB: {}",
        savedProduct);

    return new ProductDTO(savedProduct);
  }
}

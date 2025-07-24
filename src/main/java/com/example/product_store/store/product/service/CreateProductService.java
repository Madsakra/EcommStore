package com.example.product_store.store.product.service;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.exceptions.CategoryNotFoundException;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreateProductService {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;
  private final CategoryRepository categoryRepository;
  public static final Logger logger = LoggerFactory.getLogger(CreateProductService.class);

  public CreateProductService(ProductRepository productRepository, ProductValidator productValidator, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
      this.categoryRepository = categoryRepository;
  }

  public ProductDTO execute(String jti, ProductRequestDTO requestDTO) {
    // set createdBy to current User
    // no need for any checks since it is already done by spring security
    requestDTO.setCreatedBy(jti);

    logger.info("CreateProductService: The product information before saving into DB: {}", requestDTO);

    // VALIDATE THE PRODUCT
    // ANY ERRORS IN PRODUCT VALIDATOR WILL THROW AND STOP THE OPERATION
    productValidator.execute(requestDTO);

    // GET THE LIST OF CATEGORIES FROM DB
    List<Category> fullCategories = requestDTO.getCategories().stream()
            .map(dto -> categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new CategoryNotFoundException("Invalid category ID: " + dto.getId())))
            .toList();

    // CONSTRUCT THE PRODUCT
    Product product = new Product(requestDTO);
    // SET THE CATEGORY
    product.setCategories(fullCategories);

    // SAVE THE CATEGORY
    Product savedProduct = productRepository.save(product);
    logger.info("CreateProductService: The product information after saving into DB: {}", savedProduct);

    // return product as a dto
    return new ProductDTO(savedProduct);
  }
}

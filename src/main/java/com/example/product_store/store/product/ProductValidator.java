package com.example.product_store.store.product;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductValidator {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductValidator(ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
      this.categoryRepository = categoryRepository;
  }

  public void execute(Product product, boolean isUpdate) {

    if (product.getTitle() == null ||product.getTitle().isBlank()) {
      throw new ProductNotValidException("Product Title should not be empty or null!");
    }

    if ( product.getStock() == null || product.getStock() < 0) {
      throw new ProductNotValidException("Product stock should not be null or negative");
    }

    if ( product.getPrice() == null || product.getPrice().compareTo(BigDecimal.valueOf(0))<=0) {
      throw new ProductNotValidException("Product price should not be 0, null or negative");
    }

    if (!isUpdate && productRepository.existsByTitleAndPrice(product.getTitle(), product.getPrice())) {
      throw new ProductNotValidException("Duplicate product exists!");
    }

    if (product.getCategories().isEmpty()){
      throw new ProductNotValidException("Product does not have any categories!");
    }


    // CHECK CATEGORY ALSO
    for (Category cat: product.getCategories()){
        if (!categoryRepository.existsById(cat.getId()))
        {
          throw new ProductNotValidException("Failed to create product due to invalid category");
        }
      }



  }
}

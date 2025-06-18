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

    if (StringUtils.isEmpty(product.getTitle())) {
      throw new ProductNotValidException("Product Title is empty");
    }

    if (product.getStock() <= 0) {
      throw new ProductNotValidException("Product stock should not be 0 or negative");
    }

    if (product.getPrice().compareTo(BigDecimal.valueOf(0))<=0) {
      throw new ProductNotValidException("Product price should not be 0 or negative");
    }

    if (!isUpdate && productRepository.existsByTitleAndPrice(product.getTitle(), product.getPrice())) {
      throw new ProductNotValidException("Duplicate product exists!");
    }


    if (!product.getCategories().isEmpty())
    {
      // CHECK CATEGORY ALSO
      for (Category cat: product.getCategories()){
        if (!categoryRepository.existsById(cat.getId()))
        {
          throw new ProductNotValidException("Failed to create product due to invalid category");
        }
      }
    }


  }
}

package com.example.product_store.store.category.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.Command;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class DeleteCategoryService implements Command<String, Void> {

  private final CategoryRepository categoryRepository;

  public DeleteCategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_CATEGORIES, key = CacheConstants.ALL_CATEGORIES_KEY),
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = CacheConstants.ALL_PRODUCTS_KEY)
      })
  public Void execute(String id) {
    Optional<Category> categoryOptional = categoryRepository.findById(id);
    if (categoryOptional.isPresent()) {
      categoryRepository.deleteById(id);
      return null;
    }

    throw new ProductNotFoundException("Product does not exist based on id!");
  }
}

package com.example.product_store.store.category.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.Command;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.UpdateCategoryCommand;
import com.example.product_store.store.category.exceptions.CategoryNotFoundException;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.model.CategoryDTO;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UpdateCategoryService implements Command<UpdateCategoryCommand, CategoryDTO> {

  private final CategoryRepository categoryRepository;
  private final CategoryValidator categoryValidator;

  public UpdateCategoryService(CategoryRepository categoryRepository, CategoryValidator categoryValidator) {
    this.categoryRepository = categoryRepository;
    this.categoryValidator = categoryValidator;
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_CATEGORIES, key = CacheConstants.ALL_CATEGORIES_KEY),
        @CacheEvict(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = CacheConstants.ALL_PRODUCTS_KEY)
      },
      put = {
        @CachePut(cacheNames = CacheConstants.GET_ALL_CATEGORIES, key = CacheConstants.ALL_CATEGORIES_KEY),
        @CachePut(cacheNames = CacheConstants.GET_ALL_PRODUCTS, key = CacheConstants.ALL_PRODUCTS_KEY)
      })
  public CategoryDTO execute(UpdateCategoryCommand command) {

    // 1. Find Category in db first
    Optional<Category> categoryOptional = categoryRepository.findById(command.getId());
    if (categoryOptional.isPresent()) {
      Category category = command.getCategory();
      category.setId(command.getId());
      categoryValidator.execute(category, true);
      categoryRepository.save(category);
      return new CategoryDTO(category);
    }

    throw new CategoryNotFoundException();
  }
}

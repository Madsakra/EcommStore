package com.example.product_store.store.category.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.Command;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.model.CategoryDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CreateCategoryService implements Command<Category, CategoryDTO> {

  private final CategoryRepository categoryRepository;
  private final CategoryValidator categoryValidator;

  public CreateCategoryService(CategoryRepository categoryRepository, CategoryValidator categoryValidator) {
    this.categoryRepository = categoryRepository;
    this.categoryValidator = categoryValidator;
  }

  @Override
  @CacheEvict(cacheNames = CacheConstants.GET_ALL_CATEGORIES, key = CacheConstants.ALL_CATEGORIES_KEY)
  public CategoryDTO execute(Category category) {

    categoryValidator.execute(category, false);
    Category savedCategory = categoryRepository.save(category);
    return new CategoryDTO(savedCategory);
  }
}

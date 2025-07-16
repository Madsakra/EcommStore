package com.example.product_store.store.category;

import com.example.product_store.store.category.exceptions.CategoryNotValidException;
import com.example.product_store.store.category.exceptions.DuplicateCategoryException;
import com.example.product_store.store.category.model.Category;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

  private final CategoryRepository categoryRepository;

  public CategoryValidator(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void execute(Category category, boolean isUpdate) {

    if (category.getCategoryName() == null ||StringUtils.isEmpty(category.getCategoryName())) {
      throw new CategoryNotValidException();
    }

    if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
      throw new DuplicateCategoryException();
    }
  }
}

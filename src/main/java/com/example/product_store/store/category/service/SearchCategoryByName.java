package com.example.product_store.store.category.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.dto.CategoryDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchCategoryByName implements QueryBinder<String, List<CategoryDTO>> {

  private final CategoryRepository categoryRepository;

  public SearchCategoryByName(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public List<CategoryDTO> execute(String categoryName) {
    return categoryRepository.findByCategoryNameContaining(categoryName).stream().map(CategoryDTO::new).toList();
  }
}

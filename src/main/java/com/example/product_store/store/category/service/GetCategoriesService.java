package com.example.product_store.store.category.service;

import com.example.product_store.CacheConstants;
import com.example.product_store.QueryBinder;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.dto.CategoryDTO;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetCategoriesService implements QueryBinder<Void, List<CategoryDTO>> {

  private final CategoryRepository categoryRepository;

  public GetCategoriesService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  @Cacheable(cacheNames = CacheConstants.GET_ALL_CATEGORIES, key = CacheConstants.ALL_CATEGORIES_KEY)
  public List<CategoryDTO> execute(Void input) {
    List<Category> categoryList = categoryRepository.findAll();
    return categoryList.stream().map(CategoryDTO::new).toList();
  }
}

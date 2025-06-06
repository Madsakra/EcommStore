package com.example.product_store.store.category.service;

import com.example.product_store.Query;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.model.CategoryDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCategoriesService implements Query<Void, List<CategoryDTO>> {

    private final CategoryRepository categoryRepository;

    public GetCategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    @Cacheable(cacheNames = "getAllCategories",key = "'allCategories'")
    public List<CategoryDTO> execute(Void input){
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(CategoryDTO::new)
                .toList();



    }


}

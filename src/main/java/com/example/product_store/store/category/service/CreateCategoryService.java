package com.example.product_store.store.category.service;

import com.example.product_store.Command;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.model.CategoryDTO;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateCategoryService implements Command<Category , CategoryDTO> {

    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;

    public CreateCategoryService(CategoryRepository categoryRepository, CategoryValidator categoryValidator) {
        this.categoryRepository = categoryRepository;
        this.categoryValidator = categoryValidator;
    }


    @Override
    @CacheEvict(cacheNames = "getAllCategories",key = "'allCategories'")
    public CategoryDTO execute(Category category){

        categoryValidator.execute(category,false);
        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory);

    }


}

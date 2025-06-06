package com.example.product_store.store.category.service;

import com.example.product_store.Command;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.UpdateCategoryCommand;
import com.example.product_store.store.category.exceptions.CategoryNotFoundException;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.model.CategoryDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                    @CacheEvict(cacheNames = "getAllCategories",key = "'allCategories'"),
                    @CacheEvict(cacheNames = "getAllProducts",key = "'allProducts'")
            },
            put = {
                    @CachePut(cacheNames = "getAllCategories",key = "'allCategories'"),
                    @CachePut(cacheNames = "getAllProducts",key = "'allProducts'")
            }
    )
    public CategoryDTO execute(UpdateCategoryCommand command) {
        Category existing = categoryRepository.findById(command.getId())
                .orElseThrow(CategoryNotFoundException::new);

        // Update only allowed fields (in this case, just categoryName)
        Category updated = command.getCategory();
        existing.setCategoryName(updated.getCategoryName());

        categoryValidator.execute(existing, true);
        categoryRepository.save(existing);

        return new CategoryDTO(existing);
    }

}

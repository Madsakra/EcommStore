package com.example.product_store.store.category.service;

import com.example.product_store.Command;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteCategoryService implements Command<String,Void> {

    private final CategoryRepository categoryRepository;

    public DeleteCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "getAllCategories",key = "'allCategories'")

            }
    )
    public Void execute(String id){
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()){
            categoryRepository.deleteById(id);
            return null;
        }

        throw new ProductNotFoundException();
    }
}

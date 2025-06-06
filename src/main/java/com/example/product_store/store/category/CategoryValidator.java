package com.example.product_store.store.category;


import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    private CategoryRepository categoryRepository;

    public CategoryValidator(CategoryRepository categoryRepository) {
       this.categoryRepository = categoryRepository;
    }

    public void execute(Category category, boolean isUpdate) {

        if (StringUtils.isEmpty(category.getCategoryName())) {
            throw new ProductNotValidException("Product Title is empty");
        }

        if (!isUpdate && categoryRepository.existsByCategoryName(
                category.getCategoryName())){
            throw new ProductNotValidException("Duplicate category exists!");
        }





    }
}



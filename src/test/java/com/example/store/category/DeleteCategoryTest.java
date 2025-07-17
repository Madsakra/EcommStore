package com.example.store.category;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.service.DeleteCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DeleteCategoryService deleteCategoryService;

    @Test
    void testExecute_success_shouldReturnVoid(){
        // Setup
        String categoryId = "cat-123";
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Execute
        assertDoesNotThrow(() -> deleteCategoryService.execute(categoryId));

        // Verify
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).deleteById(categoryId);

    }

}

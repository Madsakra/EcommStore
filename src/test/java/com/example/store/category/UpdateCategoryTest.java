package com.example.store.category;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.UpdateCategoryCommand;
import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.exceptions.CategoryNotFoundException;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.service.UpdateCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidator categoryValidator;

    @InjectMocks
    private UpdateCategoryService updateCategoryService;

    @Test
    void testExecute_categoryExists_shouldUpdateAndReturnDTO(){
        // GIVEN
        String categoryId = "cat-123";
        Category oldCategory = new Category(categoryId,"old name");

        Category updatedCategory = new Category(null,"New Name");
        UpdateCategoryCommand command = new UpdateCategoryCommand(categoryId,updatedCategory);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(oldCategory));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(oldCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        // ACT
        CategoryDTO result = updateCategoryService.execute(command);

        // ASSERT EQUAL
        assertNotNull(result);
        assertEquals(categoryId,result.getId());
        assertEquals("New Name", result.getCategoryName());

        verify(categoryRepository).findById(categoryId);
        verify(categoryValidator).execute(updatedCategory);
        verify(categoryRepository).save(updatedCategory);
    }
    @Test
    void testExecute_categoryDoesNotExist_shouldThrowException() {
        // GIVEN
        String categoryId = "missing-cat";
        UpdateCategoryCommand command = mock(UpdateCategoryCommand.class);
        when(command.getId()).thenReturn(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(CategoryNotFoundException.class, () -> updateCategoryService.execute(command));

        verify(categoryRepository).findById(categoryId);
        verify(categoryValidator, never()).execute(any());
        verify(categoryRepository, never()).save(any());
    }


}

package com.example.store.category;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.service.GetCategoriesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCategoriesTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetCategoriesService getCategoriesService;

    @Test
    void testExecute_success_shouldReturnCategoryDTO(){
        // GIVEN
        Category cat1 = new Category("cat-1", "Fruits");
        Category cat2 = new Category("cat-2", "Vegetables");

        List<Category> categoryList = List.of(cat1,cat2);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // WHEN
        List<CategoryDTO> categoryDTOList = getCategoriesService.execute(null);
        assertNotNull(categoryDTOList);
        assertEquals(2,categoryDTOList.size());
        assertEquals("cat-1", categoryDTOList.get(0).getId());
        assertEquals("Fruits", categoryDTOList.get(0).getCategoryName());

        assertEquals("cat-2", categoryDTOList.get(1).getId());
        assertEquals("Vegetables", categoryDTOList.get(1).getCategoryName());

        verify(categoryRepository).findAll();

    }


}

package com.example.store.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.product_store.store.category.CategoryErrorMessages;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.exceptions.CategoryNotValidException;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.service.CreateCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private CategoryValidator categoryValidator;
  @InjectMocks private CreateCategoryService createCategoryService;

  @Test
  void testCreateCategoryService_success_shouldReturnCategoryDTO() {

    // GIVEN
    String categoryId = "cat-1234";
    String categoryName = "apple";
    Category mockedCategory = new Category(categoryId, categoryName);

    // RESULT RETURNED FROM SAVING IN REPO
    Category expectedCategory = new Category(categoryId, categoryName);

    // WHEN
    when(categoryRepository.save(mockedCategory)).thenReturn(expectedCategory);
    // MOCK BEHAVIOR
    doNothing().when(categoryValidator).execute(mockedCategory);

    CategoryDTO returnedDTO = createCategoryService.execute(mockedCategory);

    // ASSERT EQUALS
    assertEquals(categoryId, returnedDTO.getId());
    assertEquals(categoryName, returnedDTO.getCategoryName());
  }

  @Test
    void testCreateCategoryService_whenCategoryIsNull_shouldThrowException(){
      CategoryNotValidException ex = assertThrows(CategoryNotValidException.class,
              ()-> createCategoryService.execute(null));

      assertEquals(CategoryErrorMessages.CATEGORY_NOT_VALID.getMessage(),ex.getMessage());
  }

}

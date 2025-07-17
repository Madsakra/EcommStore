package com.example.store.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.store.category.CategoryErrorMessages;
import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.CategoryValidator;
import com.example.product_store.store.category.exceptions.CategoryNotValidException;
import com.example.product_store.store.category.exceptions.DuplicateCategoryException;
import com.example.product_store.store.category.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryValidatorTest {

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private CategoryValidator categoryValidator;

  @Test
  void testExecute_whenCategoryIsNormal() {
    // GIVEN
    Category mockedCategory = new Category();
    mockedCategory.setCategoryName("validCategory");

    // WHEN
    when(categoryRepository.existsByCategoryName("validCategory")).thenReturn(false);
    // ASSERT DOES NOT THROW
    assertDoesNotThrow(() -> categoryValidator.execute(mockedCategory));

    // verify method called
    verify(categoryRepository).existsByCategoryName("validCategory");
  }

  @Test
    void testExecute_whenCategoryNameIsNull_shouldThrowNotValidException(){
    // GIVEN
    Category mockedCategory = new Category();

    // ASSERT AND THROW
      CategoryNotValidException ex = assertThrows(CategoryNotValidException.class,()->categoryValidator.execute(mockedCategory));
      assertEquals(CategoryErrorMessages.CATEGORY_NOT_VALID.getMessage(),ex.getMessage());
  }

  @Test
  void testExecute_duplicateCategoryExist_shouldThrowDuplicateCategoryException(){
    // GIVEN
    Category mockedCategory = new Category();
    mockedCategory.setCategoryName("validCategory");

    // WHEN
    when(categoryRepository.existsByCategoryName(mockedCategory.getCategoryName())).thenReturn(true);

    // ASSERT THROW
    DuplicateCategoryException ex = assertThrows(DuplicateCategoryException.class,()->categoryValidator.execute(mockedCategory));
    assertEquals(CategoryErrorMessages.DUPLICATE_CATEGORY_EXCEPTION.getMessage(),ex.getMessage());
  }

}

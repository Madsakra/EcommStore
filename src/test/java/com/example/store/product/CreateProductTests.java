package com.example.store.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.repositories.ProductRepository;
import com.example.product_store.store.product.service.CreateProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CreateProductTests {
  @Mock private CategoryRepository categoryRepository;
  @Mock private ProductRepository productRepository;
  @Mock private ProductValidator productValidator;
  @InjectMocks private CreateProductService createProductService;

  // TEST PRODUCT CREATION SUCCESSFUL
  @Test
  void testCreateProduct_success_shouldReturnProductDTO() {
    // GIVEN
    String expectedJti = "user-123";
    String categoryId = "1L";

    // Prepare mocked category
    Category category = new Category();
    category.setId(categoryId);
    category.setCategoryName("Electronics");

    // Prepare request DTO
    ProductRequestDTO inputProduct = new ProductRequestDTO();
    inputProduct.setCreatedBy(expectedJti);
    inputProduct.setTitle("Test Product");
    inputProduct.setDescription("Sample");
    inputProduct.setPrice(BigDecimal.TEN);
    inputProduct.setStock(100);
    inputProduct.setCategories(List.of(new CategoryDTO(category)));


    // Prepare expected saved product
    Product savedProduct = new Product();
    savedProduct.setId("214214");
    savedProduct.setCreatedBy(expectedJti);

    // STUBS
    doNothing().when(productValidator).execute(inputProduct);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // WHEN
    ProductDTO result = createProductService.execute(expectedJti, inputProduct);

    // THEN
    assertNotNull(result);
    assertEquals(savedProduct.getId(), result.getId());
    assertEquals(expectedJti, result.getCreatedBy());

    verify(productValidator).execute(inputProduct);
    verify(categoryRepository).findById(categoryId);

    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(productCaptor.capture());

    Product actualSavedProduct = productCaptor.getValue();
    assertEquals(expectedJti, actualSavedProduct.getCreatedBy());
    assertEquals("Test Product", actualSavedProduct.getTitle());
    assertEquals(1, actualSavedProduct.getCategories().size());
    assertEquals(categoryId, actualSavedProduct.getCategories().get(0).getId());
  }


  @Test
  void testCreateProduct_whenJTIIsNull_ShouldThrowNullPointerException() {
    // GIVEN
    ProductRequestDTO inputProduct = new ProductRequestDTO();

    // ERROR WILL BE THROW BEFORE VALIDATOR COMES IN
    // ACT & ASSERT
    assertThrows(
        NullPointerException.class,
        () -> {
          createProductService.execute(null, inputProduct);
        });
  }
}

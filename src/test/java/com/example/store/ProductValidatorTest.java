package com.example.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductValidatorTest {

  @Mock private ProductRepository productRepository;

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private ProductValidator validator;



  @Test
  void testProductValidator_whenTitleIsNull_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle(null); // Explicit null
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product Title should not be empty or null!", ex.getMessage());
  }

  @Test
  void testProductValidator_whenTitleIsEmpty_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle(""); // empty string
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product Title should not be empty or null!", ex.getMessage());
  }

  @Test
  void testProductValidator_whenStockIsNull_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle("product1");
    product.setStock(null); // Null quantity
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product stock should not be null or negative", ex.getMessage());
  }

  @Test
  void testProductValidator_whenStockIsNegative_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle("product1");
    product.setStock(-12); // Null quantity
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product stock should not be null or negative", ex.getMessage());
  }

  @Test
  void testProductValidator_whenPriceIsNull_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle("product1");
    product.setStock(100);
    product.setPrice(null);
    ; // Null quantity
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product price should not be 0, null or negative", ex.getMessage());
  }

  @Test
  void testProductValidator_whenPriceIsZero_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle("product1");
    product.setStock(100);
    product.setPrice(BigDecimal.valueOf(0));
    ; // Null quantity
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product price should not be 0, null or negative", ex.getMessage());
  }

  @Test
  void testProductValidator_whenPriceIsNegative_ShouldThrowProductNotValidException() {
    Product product = new Product();
    product.setTitle("product1");
    product.setStock(100);
    product.setPrice(BigDecimal.valueOf(-100));
    ; // Null quantity
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(product, false));
    assertEquals("Product price should not be 0, null or negative", ex.getMessage());
  }

  @Test
  void testProductValidator_duplicateProduct_ShouldThrowProductNotValidException() {
    // GIVEN
    Product testProduct1 = new Product();
    testProduct1.setTitle("testProduct1");
    testProduct1.setStock(100);
    testProduct1.setPrice(BigDecimal.valueOf(1000));

    // WHEN
    when(productRepository.existsByTitleAndPrice(
            testProduct1.getTitle(), testProduct1.getPrice()))
        .thenReturn(true);

    // ASSERT THROW
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(testProduct1, false));

    assertEquals("Duplicate product exists!", ex.getMessage());
  }

  @Test
  void testProductValidator_EmptyCategory_ShouldThrowProductNotValidException() {
    // GIVEN
    Product testProduct1 = new Product();
    testProduct1.setTitle("testProduct1");
    testProduct1.setStock(100);
    testProduct1.setPrice(BigDecimal.valueOf(1000));
    testProduct1.setCategories(Collections.emptyList());
    // ASSERT THROW
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(testProduct1, false));

    assertEquals("Product does not have any categories!", ex.getMessage());
  }

  @Test
  void testProductValidator_InvalidCategoryId_ShouldThrowProductNotValidException() {
    // GIVEN
    Category invalidCategory = new Category();
    invalidCategory.setId("invalidID"); // this ID will be treated as non-existent

    Product testProduct1 = new Product();
    testProduct1.setTitle("testProduct1");
    testProduct1.setStock(100);
    testProduct1.setPrice(BigDecimal.valueOf(1000));
    testProduct1.setCategories(List.of(invalidCategory));

    when(categoryRepository.existsById("invalidID")).thenReturn(false);
    // ASSERT THROW
    ProductNotValidException ex =
        assertThrows(
            ProductNotValidException.class, () -> validator.execute(testProduct1, false));

    assertEquals("Failed to create product due to invalid category", ex.getMessage());
  }
}

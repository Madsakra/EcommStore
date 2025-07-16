package com.example.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.repositories.ProductRepository;
import com.example.product_store.store.product.service.CreateProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateProductTests {
  @Mock private ProductRepository productRepository;
  @Mock private ProductValidator productValidator;
  @InjectMocks private CreateProductService createProductService;

  // TEST PRODUCT CREATION SUCCESSFUL
  @Test
  void testCreateProduct_success_shouldReturnProductDTO() {

    // GIVEN
    String expectedJti = "user-123";
    ProductRequestDTO inputProduct = new ProductRequestDTO();
    inputProduct.setCreatedBy(expectedJti);
    Product mockedProduct = new Product(inputProduct);

    Product savedProduct = new Product();
    savedProduct.setId("214214");
    savedProduct.setCreatedBy(expectedJti);

    // WHEN
    // createProductService.execute()-> will call for jwt
    // following chain below will return jwt

    // do nothing for validator, assume it will pass
    doNothing().when(productValidator).execute(inputProduct, false);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // Act
    ProductDTO result = createProductService.execute(expectedJti, inputProduct);

    // Assert
    assertNotNull(result);
    assertEquals(savedProduct.getId(), result.getId());
    assertEquals(savedProduct.getCreatedBy(), result.getCreatedBy());

    verify(productValidator).execute(inputProduct, false);
    verify(productRepository).save(mockedProduct);
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

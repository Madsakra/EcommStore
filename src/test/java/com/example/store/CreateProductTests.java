package com.example.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.service.CreateProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;



public class CreateProductTests {
  @Mock private ProductRepository productRepository;

  @InjectMocks private CreateProductService createProductService;

  @Mock private ProductValidator productValidator;

  @Mock private Authentication authentication;

  @Mock private SecurityContext securityContext;

  @BeforeEach
  public void setup() {
    // initialize the repository & the service
    MockitoAnnotations.openMocks(this);
  }

  // TEST PRODUCT CREATION SUCCESSFUL
  @Test
  void testCreateProduct_success_shouldReturnProductDTO() {

    // GIVEN
    String expectedJti = "user-123";
    Product inputProduct = new Product();

    Product savedProduct = new Product();
    savedProduct.setId("214214");
    savedProduct.setCreatedBy(expectedJti);

    // WHEN
    // createProductService.execute()-> will call for jwt
    // following chain below will return jwt
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(expectedJti);
    SecurityContextHolder.setContext(securityContext);

    // do nothing for validator, assume it will pass
    doNothing().when(productValidator).execute(inputProduct, false);
    when(productRepository.save(inputProduct)).thenReturn(savedProduct);

    // Act
    ProductDTO result = createProductService.execute(inputProduct);

    // Assert
    assertNotNull(result);
    assertEquals(savedProduct.getId(), result.getId());
    assertEquals(savedProduct.getCreatedBy(), result.getCreatedBy());

    verify(productValidator).execute(inputProduct, false);
    verify(productRepository).save(inputProduct);
  }

  @Test
  void testCreateProduct_whenSecurityContextIsNull_ShouldThrowNullPointerException() {
    // GIVEN
    SecurityContextHolder.clearContext();
    Product inputProduct = new Product();

    // ERROR WILL BE THROW BEFORE VALIDATOR COMES IN
    // ACT & ASSERT
    assertThrows(
        NullPointerException.class,
        () -> {
          createProductService.execute(inputProduct);
        });
  }

  @Test
  void testCreateProduct_WhenAuthenticationIsNull_ShouldThrowNullPointerException() {
    // GIVEN
    SecurityContextHolder.setContext(securityContext);
    Product inputProduct = new Product();

    // WHEN
    when(securityContext.getAuthentication()).thenReturn(null);

    // ERROR WILL BE THROW BEFORE VALIDATOR COMES IN
    // ACT & ASSERT
    assertThrows(
        NullPointerException.class,
        () -> {
          createProductService.execute(inputProduct);
        });
  }

  @Test
  void testCreateProduct_WhenPrincipalIsNull_ShouldThrowNullPointerException() {
    // GIVEN
    SecurityContextHolder.setContext(securityContext);
    Product inputProduct = new Product();

    // WHEN
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(null);

    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () -> {
          createProductService.execute(inputProduct);
        });
  }


}

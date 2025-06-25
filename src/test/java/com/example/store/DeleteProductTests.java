package com.example.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.service.DeleteProductService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class DeleteProductTests {
  @Mock private ProductRepository productRepository;

  @Mock private Authentication authentication;

  @Mock private SecurityContext securityContext;

  @InjectMocks private DeleteProductService deleteProductService;

  @Test
  void testDeleteProduct_withValidProductId_shouldReturnNull() {
    // GIVEN
    String mockedUserId = "user-123";
    Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(mockedUserId);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);

    String productId = "qwerty123";

    Product existingProduct = new Product();
    existingProduct.setId(productId);
    existingProduct.setCreatedBy(mockedUserId);
    // ASSERT
    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

    // ACT
    assertDoesNotThrow(() -> deleteProductService.execute(productId));
    verify(productRepository).deleteById(productId);
  }

  @Test
  void testDeleteProduct_productNotFound_shouldThrowProductNotFoundException() {
    String productId = "notFound";

    when(productRepository.findById(productId)).thenReturn(Optional.empty());
    ProductNotFoundException ex =
        assertThrows(
            ProductNotFoundException.class,
            () -> deleteProductService.execute(productId));
    assertEquals("Product does not exist based on id!", ex.getMessage());
  }

    @Test
    void testDeleteProduct_unauthorizedManagement_shouldThrowUnauthorizedException(){
        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String productId = "qwerty123";

        // Setup existing product
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setCreatedBy("differentUser");

        // WHEN
        // assumes product exist, return optional of existing product
        // IF NOT INCLUDED, WILL THROW NULLPOINTER EXCEPTION INSTEAD
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));


        // WHEN
        UnauthorizedManagement ex = assertThrows(
                UnauthorizedManagement.class,
                ()->deleteProductService.execute(productId)
        );
        assertEquals("This product does not belongs to you!", ex.getMessage());
    }


}

package com.example.store;

import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.UpdateProductCommand;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.service.UpdateProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductTests {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ProductValidator productValidator;

    @InjectMocks
    private UpdateProductService updateProductService;




    @Test
    void testUpdateProduct_withValidProduct_shouldReturnProductDTO(){

        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);


        String productId = "qwerty123";
        String expectedJti = "user-123";

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setCreatedBy(expectedJti);

        // USER PAYLOAD
        ProductRequestDTO updatedProduct = new ProductRequestDTO();
        updatedProduct.setTitle("Updated Title");
        updatedProduct.setStock(50);
        updatedProduct.setPrice(BigDecimal.valueOf(500));
        updatedProduct.setCategories(List.of(new CategoryDTO())); // dummy list

        UpdateProductCommand command = new UpdateProductCommand(productId,updatedProduct);

        // WHEN
        // assumes product exist, return optional of existing product
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Validator does nothing (no exception = valid)
        doNothing().when(productValidator).execute(any(ProductRequestDTO.class), eq(true));

        when(productRepository.save(any(Product.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        // Act
        ProductDTO result = updateProductService.execute(command);

        // Assert
        assertEquals("Updated Title", result.getTitle());
        assertEquals(BigDecimal.valueOf(500), result.getPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_productNotFound_shouldThrowProductNotFoundException(){
        String productId = "qwerty123";

        // MOCKED USER PAYLOAD
        ProductRequestDTO updatedProduct = new ProductRequestDTO();
        updatedProduct.setTitle("Updated Title");
        updatedProduct.setStock(50);
        updatedProduct.setPrice(BigDecimal.valueOf(500));
        updatedProduct.setCategories(List.of(new CategoryDTO())); // dummy list

        UpdateProductCommand command = new UpdateProductCommand(productId,updatedProduct);
        // ❗ Mock the repository to simulate "product not found"
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ProductNotFoundException ex =
                assertThrows(
                        ProductNotFoundException.class, () -> updateProductService.execute(command));
        assertEquals("Product does not exist based on id!", ex.getMessage());
    }

    @Test
    void testUpdateProduct_unauthorizedManagement_shouldThrowUnauthorisedException(){
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

        // Setup the update command (user payload)
        ProductRequestDTO updatedProduct = new ProductRequestDTO();
        updatedProduct.setTitle("Updated Title");
        updatedProduct.setStock(50);
        updatedProduct.setPrice(BigDecimal.valueOf(500));
        updatedProduct.setCategories(List.of(new CategoryDTO())); // dummy
        UpdateProductCommand command = new UpdateProductCommand(productId, updatedProduct);

        // WHEN
        // assumes product exist, return optional of existing product
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        UnauthorizedManagement ex = assertThrows(
                UnauthorizedManagement.class,
                () -> updateProductService.execute(command)
        );

        assertEquals("This product does not belongs to you!", ex.getMessage());

    }

}

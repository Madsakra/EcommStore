package com.example.user_favourites;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.user_favourites.service.DeleteUserFavouriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DeleteUserFavouritesTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteUserFavouriteService deleteUserFavouriteService;

    @Test
    void testExecute_shouldReturnVoid(){
        // PRODUCT ID INSERTED BY USER
        String productId = "targetProduct";

        // GIVEN
        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // MOCK PRODUCT
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("addedProduct");

        Set<Product> mockProductSet = new HashSet<>();
        mockProductSet.add(mockProduct);

        // MOCK ACCOUNT
        Account mockAccount = new Account();
        mockAccount.setId(mockedUserId);
        mockAccount.setFavouriteProducts(mockProductSet);

        // WHEN
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(accountRepository.findById(mockedUserId)).thenReturn(Optional.of(mockAccount));

        // ACT
        deleteUserFavouriteService.execute(productId);

        // ASSERT
        assertFalse(mockAccount.getFavouriteProducts().contains(mockProduct), "Product should be removed from favourites");
        verify(productRepository).findById(productId);
        verify(accountRepository).findById(mockedUserId);
        verify(accountRepository).save(mockAccount);
    }

    @Test
    void testExecute_ProductNotFound_shouldThrowProductNotFoundException() {
        // GIVEN
        // PRODUCT ID INSERTED BY USER
        String productId = "targetProduct";
        // WHEN
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        // ASSERT THROWS
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, ()-> deleteUserFavouriteService.execute(productId));
        assertEquals("Product not found with the given id", ex.getMessage());
    }

    @Test
    void testExecute_AccountNotFound_shouldThrowAccountNotFoundException(){
        // PRODUCT ID INSERTED BY USER
        String productId = "targetProduct";

        // GIVEN
        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("addedProduct");

        // WHEN
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(accountRepository.findById(mockedUserId)).thenReturn(Optional.empty());

        // ASSERT THROWS
        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, ()-> deleteUserFavouriteService.execute(productId));
        assertEquals("Account not found with current JWT", ex.getMessage());

    }


}

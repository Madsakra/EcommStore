package com.example.user_favourites;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.user_favourites.dto.UserFavouriteDTO;
import com.example.product_store.user_favourites.service.AddUserFavouriteService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddUserFavouriteTest {

  @Mock private AccountRepository accountRepository;

  @Mock private ProductRepository productRepository;

  @InjectMocks private AddUserFavouriteService addUserFavouriteService;

  @Test
  void testExecute_ShouldReturnUserFavouriteDTO() {
    // PRODUCT ID INSERTED BY USER
    String productId = "targetProduct";

    // GIVEN
    String mockedUserId = "user-123";
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
    when(accountRepository.findById(mockedUserId)).thenReturn(Optional.of(mockAccount));

    when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
    // ACT
    UserFavouriteDTO result = addUserFavouriteService.execute(mockedUserId, productId);

    // ASSERT

    assertNotNull(result);
    assertEquals(1, result.getFavouriteProducts().size());
    assertTrue(
        result.getFavouriteProducts().stream()
            .anyMatch(
                p -> p.getId().equals(productId) && p.getTitle().equals("addedProduct")));
    verify(productRepository).findById(productId);
    verify(accountRepository).findById(mockedUserId);
  }

  @Test
  void testExecute_ProductNotFound_shouldThrowProductNotFoundException() {
    // GIVEN
    String expectedJti = "hash-1234";
    // PRODUCT ID INSERTED BY USER
    String productId = "targetProduct";
    // WHEN
    // Mock account
    Account mockAccount = new Account();
    mockAccount.setId(expectedJti);

    mockAccount.setFavouriteProducts(Collections.emptySet());

    // WHEN
    when(accountRepository.findById(expectedJti)).thenReturn(Optional.of(mockAccount));
    when(productRepository.findById(productId)).thenReturn(Optional.empty());
    // ASSERT THROWS
    ProductNotFoundException ex =
        assertThrows(
            ProductNotFoundException.class,
            () -> addUserFavouriteService.execute(expectedJti, productId));
    assertEquals("Product not found with the given id", ex.getMessage());
  }

  @Test
  void testExecute_AccountNotFound_shouldThrowAccountNotFoundException() {
    // PRODUCT ID INSERTED BY USER
    String productId = "targetProduct";

    // GIVEN
    String expectedJti = "hash-1234";
    Product mockProduct = new Product();
    mockProduct.setId(productId);
    mockProduct.setTitle("addedProduct");

    // ASSERT THROWS
    AccountNotFoundException ex =
        assertThrows(
            AccountNotFoundException.class,
            () -> addUserFavouriteService.execute(expectedJti, productId));
    assertEquals("Account not found with current JWT", ex.getMessage());
  }
}

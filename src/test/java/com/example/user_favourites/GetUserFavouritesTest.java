package com.example.user_favourites;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;

import com.example.product_store.store.product.model.Product;
import com.example.product_store.user_favourites.dto.UserFavouriteDTO;
import com.example.product_store.user_favourites.service.GetUserFavouriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;


@ExtendWith(MockitoExtension.class)
public class GetUserFavouritesTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private GetUserFavouriteService getUserFavouriteService;


    @Test
    void testExecute_shouldReturnUserFavouriteDTO(){
        String productId = "expected123";

        // GIVEN
        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // MOCK PRODUCT
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("testProduct");

        // Mock account
        Account mockAccount = new Account();
        mockAccount.setId(mockedUserId);
        Set<Product> mockProductSet = new HashSet<>();
        mockProductSet.add(mockProduct);
        mockAccount.setFavouriteProducts(mockProductSet);

        // WHEN
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(accountRepository.findById(mockedUserId)).thenReturn(Optional.of(mockAccount));

        // ACT
        UserFavouriteDTO result = getUserFavouriteService.execute(null);


        // ASSERT
        assertTrue(result.getFavouriteProducts().stream()
                .anyMatch(p -> p.getId().equals(productId) && p.getTitle().equals("testProduct")));
        assertEquals(1,result.getFavouriteProducts().size());
        verify(accountRepository).findById(mockedUserId);
    }

    @Test
    void testExecute_NoFavourites_shouldReturnEmptySet(){
        // GIVEN
        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);


        // Mock account
        Account mockAccount = new Account();
        mockAccount.setId(mockedUserId);


        mockAccount.setFavouriteProducts(Collections.emptySet());

        // WHEN
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(accountRepository.findById(mockedUserId)).thenReturn(Optional.of(mockAccount));

        // ACT
        UserFavouriteDTO result = getUserFavouriteService.execute(null);


        // ASSERT
        assertNotNull(result);
        assertEquals(0,result.getFavouriteProducts().size());
        verify(accountRepository).findById(mockedUserId);
    }


    @Test
    void testExecute_AccountNotFound_shouldThrowAccountNotFoundException(){

        // GIVEN
        String mockedUserId = "user-123";
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // WHEN
        when(auth.getPrincipal()).thenReturn(mockedUserId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(accountRepository.findById(mockedUserId)).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class,()->getUserFavouriteService.execute(null));
        assertEquals("Account not found based on current user ID", ex.getMessage());

    }


}

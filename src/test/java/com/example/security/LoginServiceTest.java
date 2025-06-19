package com.example.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.authentication.model.Account;

import com.example.product_store.authentication.service.LoginService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

  @Mock private AccountRepository accountRepository;

  @InjectMocks private LoginService loginService;


  // TEST USER EXISTS
  @Test
  public void testLoadUserByUsername_UserExists() {
    // ARRANGE
    Account mockAccount = new Account();
    mockAccount.setUserName("johnDoe");
    mockAccount.setPassword("$2a$10$examplehash21412");

    when(accountRepository.findUserByEmailOrUserName("johnDoe")).thenReturn(Optional.of(mockAccount));

    // ACT
    UserDetails userDetails =  loginService.loadUserByUsername("johnDoe");

    // ASSERT
    assertEquals("johnDoe",userDetails.getUsername());
  }

  @Test
  public void testLoadUserByUsername_UserDoesNotExist(){
    // ARRANGE
    String loginIdentifier = "nonexistent@gmail.com";
    when(accountRepository.findUserByEmailOrUserName(loginIdentifier)).thenReturn(Optional.empty());

    // ACT & ASSERT
    assertThrows(UsernameNotFoundException.class, ()->{
      loginService.loadUserByUsername(loginIdentifier);
    });


  }

}

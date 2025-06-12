package com.example.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.model.Account;
import com.example.product_store.security.model.MyUserDetails;
import com.example.product_store.security.service.LoginService;
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
    // Arrange
    String loginIdentifier = "colin@gmail.com";
    Account account = new Account();
    when(accountRepository.findUserByEmailOrUserName(loginIdentifier))
        .thenReturn(Optional.of(account));

    // ACT
    UserDetails userDetails = loginService.loadUserByUsername(loginIdentifier);

    // ASSERT
    assertNotNull(userDetails);
    assertInstanceOf(MyUserDetails.class, userDetails);
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

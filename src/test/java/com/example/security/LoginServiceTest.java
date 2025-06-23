package com.example.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.dto.LoginRequestDTO;
import com.example.product_store.authentication.errors.InvalidUserDetailsException;
import com.example.product_store.authentication.jwt.JwtUtil;
import com.example.product_store.authentication.jwt.MyUserDetails;


import com.example.product_store.authentication.service.LoginService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {


  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks private LoginService loginService;


  // WHEN USER LOGS IN WITH NULL AS USERNAME / EMAIL
  @Test
  public void testWhenIdentifierIsNull() {
      LoginRequestDTO loginRequestDTO = new LoginRequestDTO(null,"password");

    InvalidUserDetailsException ex = assertThrows(
            InvalidUserDetailsException.class,()-> loginService.execute(loginRequestDTO)
    );
    assertEquals("Username/Email identifier cannot be null!",ex.getMessage());
  }

  // TEST WHEN USER LOGS IN WITH PASSWORD AS NULL
  @Test
  public void testWhenPasswordIsNull(){
    LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user1",null);
    InvalidUserDetailsException ex = assertThrows(
            InvalidUserDetailsException.class,()-> loginService.execute(loginRequestDTO)
    );
    assertEquals("Password cannot be null!",ex.getMessage());
  }

  @Test
  void testWhenIdentifierIsEmpty() {
    LoginRequestDTO dto = new LoginRequestDTO("", "password");

    InvalidUserDetailsException ex = assertThrows(
            InvalidUserDetailsException.class,
            () -> loginService.execute(dto)
    );

    assertEquals("Username/email identifier is empty", ex.getMessage());
  }

  @Test
  void testWhenPasswordIsEmpty() {
    LoginRequestDTO dto = new LoginRequestDTO("user", "");

    InvalidUserDetailsException ex = assertThrows(
            InvalidUserDetailsException.class,
            () -> loginService.execute(dto)
    );
    assertEquals("Password is empty", ex.getMessage());
  }

  @Test
  void testWhenLoginIsSuccessful(){
    // Given
    String expectedJwt = "jwt-test-token";
    String identifier = "user";
    String password =  "pass124";
    LoginRequestDTO dto  = new LoginRequestDTO(identifier,password);

    MyUserDetails  myUserDetails = mock(MyUserDetails.class);
    Authentication auth = mock(Authentication.class);

    when(auth.getPrincipal()).thenReturn(myUserDetails);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)){
      jwtUtilMock.when(()->JwtUtil.generateToken(myUserDetails)).thenReturn(expectedJwt);

      // When
      String result = loginService.execute(dto);

      // Then
      assertEquals(expectedJwt,result);
      verify(authenticationManager).authenticate(any());
      jwtUtilMock.verify(()->JwtUtil.generateToken(myUserDetails));
    }
  }

  // TEST FOR WRONG ACCOUNT DURING LOGIN
  @Test
  void testWhenLoginFails(){
    // GIVEN
    LoginRequestDTO dto = new LoginRequestDTO("wrong user","wrong password");
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad Credentials"));
    // When / Then
    BadCredentialsException ex = assertThrows(
            BadCredentialsException.class,
            () -> loginService.execute(dto)
    );

    assertEquals("Bad Credentials", ex.getMessage());
    verify(authenticationManager).authenticate(any());
  }



}

package com.example.product_store.security;

import com.example.product_store.security.errors.AccountAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccountAlreadyExistsException.class)
  public ResponseEntity<String> handleAccountAlreadyExistException(
      AccountAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleAuthException(AuthenticationException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("Error", "Authentication failed");
    response.put("message", ex.getMessage());
    response.put("status", HttpStatus.UNAUTHORIZED.value());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }
}

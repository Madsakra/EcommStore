package com.example.product_store;

import com.example.product_store.security.errors.AccountAlreadyExistsException;
import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // TEMPLATE FOR BUILDING ERROR RESPONSE
  private ResponseEntity<Map<String, Object>> buildResponseError(
      String error, String message, HttpStatus status) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", error);
    response.put("message:", message);
    response.put("status", status.value());
    return new ResponseEntity<>(response, status);
  }


  @ExceptionHandler(AccountAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleAccountAlreadyExistException(
      AccountAlreadyExistsException ex) {
    return buildResponseError("Account Exists Error", ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleAuthException(AuthenticationException ex) {
    return buildResponseError("Authentication Failed", ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccountNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleAccountNotValidException(
      AccountNotValidException ex) {
    return buildResponseError("Account not valid Error", ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UnauthorizedManagement.class)
  public ResponseEntity<Map<String, Object>> handleUnauthorizedManagementException(
      UnauthorizedManagement ex) {
    return buildResponseError("Unauthorised Management", ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler
  public ResponseEntity<Map<String,Object>> handleProductException(ProductNotValidException ex){
    return buildResponseError("Failed to create product",ex.getMessage(),HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<Map<String,Object>> handlePageException(InvalidPageRequestException ex){
    return buildResponseError("Failed to fetch produccts", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

}

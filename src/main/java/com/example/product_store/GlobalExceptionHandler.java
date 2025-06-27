package com.example.product_store;

import com.example.product_store.kafka.exceptions.InsufficientBalanceException;
import com.example.product_store.kafka.exceptions.ProductStockException;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    return buildResponseError("Failed to fetch products", ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<Map<String,Object>> handleProductNotFoundException(ProductNotFoundException ex){
    return buildResponseError("Product not found",ex.getMessage(),HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<Map<String,Object>> handleInsufficientStock(ProductStockException ex){
    return buildResponseError("Insufficient Stock error",ex.getMessage(),HttpStatus.CONFLICT);
  }

  @ExceptionHandler
  public ResponseEntity<Map<String,Object>> handleInsufficientBal(InsufficientBalanceException ex){
    return buildResponseError("Insufficient Balance in account", ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().body("Invalid type for parameter: " + ex.getName());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleJsonParseError(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest().body("Malformed JSON request: " + ex.getMessage());
  }






}

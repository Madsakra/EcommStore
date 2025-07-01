package com.example.product_store;

import com.example.product_store.order.exceptions.InsufficientBalanceException;
import com.example.product_store.order.exceptions.ProductStockException;

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

  // WHEN TYPE MISMATCH
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().body("Invalid type for parameter: " + ex.getName());
  }

  // WHEN JSON MALFORM
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleJsonParseError(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest().body("Malformed JSON request: " + ex.getMessage());
  }


}

package com.example.product_store.order;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.example.product_store.order.exceptions.OrderNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

  // WHEN INSUFFICIENT STOCK
  // HANDLE ERROR AND DISPLAY RESPONSE ENTITY TO USER
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Order not found with the given id", ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleJsonProcessingError(JsonProcessingException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Internal Error Serializing data", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

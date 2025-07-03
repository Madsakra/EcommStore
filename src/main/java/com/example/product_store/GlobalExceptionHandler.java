package com.example.product_store;


import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import org.springframework.dao.DataIntegrityViolationException;
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
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ErrorResponseTemplate.buildResponseError("Invalid Type for parameter", ex.getMessage(),HttpStatus.CONFLICT);

  }

  // WHEN JSON MALFORM
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
    return ErrorResponseTemplate.buildResponseError("Malformed JSON request", ex.getMessage(),HttpStatus.BAD_REQUEST);
  }

  // DATA INTEGRITY ERROR
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex){
    return ErrorResponseTemplate.buildResponseError("Data Integrity violation error", ex.getMessage(),HttpStatus.CONFLICT);
  }

}

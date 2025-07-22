package com.example.product_store.error_response;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseTemplate {
  public static ResponseEntity<ErrorResponse> buildResponseError(String error, String message, HttpStatus status) {
    ErrorResponse response =
        ErrorResponse.builder()
            .error(error)
            .message(message)
            .status(status.value())
            .timestamp(LocalDateTime.now().toString())
            .build();

    return new ResponseEntity<>(response, status);
  }

  // ONLY FOR JWT ERROR
  // SINCE JWT ERRORS RESIDE IN SERVLET SO HAVE TO THROW AND CATCH
  public static ErrorResponse buildError(String error, String message, HttpStatus status) {
    return ErrorResponse.builder()
            .error(error)
            .message(message)
            .status(status.value())
            .timestamp(LocalDateTime.now().toString())
            .build();
  }
}

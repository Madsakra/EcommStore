package com.example.product_store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseTemplate {
    public static ResponseEntity<ErrorResponse> buildResponseError(String error, String message, HttpStatus status) {
        ErrorResponse response = ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(response, status);
    }
}
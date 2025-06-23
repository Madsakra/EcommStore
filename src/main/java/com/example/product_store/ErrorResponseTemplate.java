package com.example.product_store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseTemplate {
    // TEMPLATE FOR BUILDING ERROR RESPONSE
    public static ResponseEntity<Map<String, Object>> buildResponseError(
            String error, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message:", message);
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }
}

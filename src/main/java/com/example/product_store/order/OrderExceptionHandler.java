package com.example.product_store.order;

import com.example.product_store.ErrorResponseTemplate;
import com.example.product_store.order.exceptions.InsufficientBalanceException;
import com.example.product_store.order.exceptions.ProductStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class OrderExceptionHandler {

    // WHEN INSUFFICIENT STOCK
    // HANDLE ERROR AND DISPLAY RESPONSE ENTITY TO USER
    @ExceptionHandler
    public ResponseEntity<Map<String,Object>> handleInsufficientStock(ProductStockException ex){
        return ErrorResponseTemplate.buildResponseError("Insufficient Stock error",ex.getMessage(), HttpStatus.CONFLICT);
    }

    // WHEN INSUFFICIENT BALANCE
    // HANDLE ERROR AND DISPLAY RESPONSE ENTITY TO USER
    @ExceptionHandler
    public ResponseEntity<Map<String,Object>> handleInsufficientBal(InsufficientBalanceException ex){
        return ErrorResponseTemplate.buildResponseError("Insufficient Balance in account", ex.getMessage(), HttpStatus.CONFLICT);
    }

}

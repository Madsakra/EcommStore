package com.example.product_store.store;

import com.example.product_store.ErrorResponseTemplate;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ProductExceptionHandler {

    // FAILURE TO CREATE PRODUCT
    @ExceptionHandler
    public ResponseEntity<Map<String,Object>> handleProductException(ProductNotValidException ex){
        return ErrorResponseTemplate.buildResponseError("Failed to create product",ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // FAILURE TO FETCH PRODUCT
    @ExceptionHandler
    public ResponseEntity<Map<String,Object>> handlePageException(InvalidPageRequestException ex){
        return ErrorResponseTemplate.buildResponseError("Failed to fetch products", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // PRODUCT NOT FOUND
    @ExceptionHandler
    public ResponseEntity<Map<String,Object>> handleProductNotFoundException(ProductNotFoundException ex){
        return ErrorResponseTemplate.buildResponseError("Product not found",ex.getMessage(),HttpStatus.NOT_FOUND);
    }

}

package com.example.product_store.store;

import com.example.product_store.ErrorResponse;
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
    public ResponseEntity<ErrorResponse> handleProductException(ProductNotValidException ex){
        return ErrorResponseTemplate.buildResponseError("Failed to create / update product due to invalid payload",ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // FAILURE TO FETCH PRODUCT
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePageException(InvalidPageRequestException ex){
        return ErrorResponseTemplate.buildResponseError("Fail to fetch non-existent products", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // PRODUCT NOT FOUND
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex){
        return ErrorResponseTemplate.buildResponseError("Unable to find product with the product id",ex.getMessage(),HttpStatus.NOT_FOUND);
    }

}

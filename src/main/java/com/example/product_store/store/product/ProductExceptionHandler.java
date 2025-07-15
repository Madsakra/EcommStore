package com.example.product_store.store.product;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {

    // FAILURE TO CREATE PRODUCT
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleProductNotValidException(ProductNotValidException ex){
        return ErrorResponseTemplate.buildResponseError("Product is not valid",ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // FAILURE TO FETCH PRODUCT
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePageException(InvalidPageRequestException ex){
        return ErrorResponseTemplate.buildResponseError("Invalid page request", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // PRODUCT NOT FOUND
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex){
        return ErrorResponseTemplate.buildResponseError("Product not found",ex.getMessage(),HttpStatus.NOT_FOUND);
    }

}

package com.example.product_store.store.category;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.example.product_store.store.category.exceptions.CategoryNotFoundException;
import com.example.product_store.store.category.exceptions.CategoryNotValidException;
import com.example.product_store.store.category.exceptions.DuplicateCategoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoryExceptionHandler {

    // FAILURE TO CREATE PRODUCT
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCategoryNotValidException(CategoryNotValidException ex){
        return ErrorResponseTemplate.buildResponseError("Category Not Valid",ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // FAILURE TO CREATE PRODUCT
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryException(DuplicateCategoryException ex){
        return ErrorResponseTemplate.buildResponseError("Duplicate Category",ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // UNABLE TO FIND CATEGORY
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException ex){
        return ErrorResponseTemplate.buildResponseError("Category Not Found.",ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

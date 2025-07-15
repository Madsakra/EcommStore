package com.example.product_store.store.product.exceptions;


// WHEN USER TRY TO USE INVALID FILTERS FOR SEARCH
public class InvalidPageRequestException extends RuntimeException {
    public InvalidPageRequestException(String message) {
        super(message);
    }
}

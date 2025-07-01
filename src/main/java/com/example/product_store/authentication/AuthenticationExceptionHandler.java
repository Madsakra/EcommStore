package com.example.product_store.authentication;

import com.example.product_store.ErrorResponseTemplate;
import com.example.product_store.authentication.errors.*;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// GLOBAL EXCEPTION HANDLER THAT DEAL WITH AUTHENTICATION MATTERS
@RestControllerAdvice
public class AuthenticationExceptionHandler {



    // FOR ACCOUNT CREATION
    // ACCOUNT ALREADY EXIST EXCEPTION
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAccountAlreadyExistException(
            AccountAlreadyExistsException ex) {
        return ErrorResponseTemplate.buildResponseError("Account already exist when trying to create.", ex.getMessage(), HttpStatus.CONFLICT);
    }


    // USED FOR CREATING ACCOUNT
    // ROLE ID DON'T EXIST IN MYSQL
    @ExceptionHandler(InvalidRoleIdException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidRoleException(InvalidRoleIdException ex){
        return ErrorResponseTemplate.buildResponseError("Role id don't exist", ex.getMessage(), HttpStatus.CONFLICT);
    }

    // USED FOR CREATING ACCOUNT
    // CALLED IN AccountValidator
    // ROLE ID AND ROLE NAME DON'T MATCH
    @ExceptionHandler(RoleMismatchException.class)
    public ResponseEntity<Map<String,Object>> handleRoleMismatchException(RoleMismatchException ex){
        return ErrorResponseTemplate.buildResponseError("Role id and Role name mismatch", ex.getMessage(), HttpStatus.CONFLICT);
    }

    //     FOR ACCOUNT CREATION
    // CALLED IN AccountValidator
    //     USED TO CHECK WHETHER PAYLOAD IS VALID
    @ExceptionHandler(AccountNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotValidException(
            AccountNotValidException ex) {
        return ErrorResponseTemplate.buildResponseError("Failed to create Account due to invalid parameters", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // USED FOR FETCHING DATA WITH USER ID
    // FOR CHECKING ACCOUNT EXISTENCE
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFoundException(
            AccountAlreadyExistsException ex) {
        return ErrorResponseTemplate.buildResponseError("Account don't exist", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // WHEN USERNAME/EMAIL OR PASSWORD IS EMPTY
    @ExceptionHandler(InvalidUserDetailsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDetailsException(
            InvalidUserDetailsException ex) {
        return ErrorResponseTemplate.buildResponseError("Invalid login credentials", ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    // USED FOR LOGGING IN
    // IF ACCOUNT PROVIDES WRONG PASSWORD
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,Object>> handleBadCredentialsException(BadCredentialsException ex){
        return ErrorResponseTemplate.buildResponseError("Failed to login due to wrong credentials",ex.getMessage(),HttpStatus.BAD_REQUEST);
    }

    // UNAUTHORIZED MANAGEMENT
    // USED TO BLOCK OUT USERS TRYING TO ACCESS OTHER ENDPOINTS NOT BELONGING TO THEIR ROLE
    @ExceptionHandler(UnauthorizedManagement.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedManagementException(
            UnauthorizedManagement ex) {
        return ErrorResponseTemplate.buildResponseError("Unauthorised Management", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }


}

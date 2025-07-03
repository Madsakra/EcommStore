package com.example.product_store.authentication;

import com.example.product_store.error_response.ErrorResponse;
import com.example.product_store.error_response.ErrorResponseTemplate;
import com.example.product_store.authentication.errors.*;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// GLOBAL EXCEPTION HANDLER THAT DEAL WITH AUTHENTICATION MATTERS
@RestControllerAdvice
public class AuthenticationExceptionHandler {

  // FOR ACCOUNT CREATION
  // ACCOUNT ALREADY EXIST EXCEPTION
  @ExceptionHandler(AccountAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleAccountAlreadyExistException(
      AccountAlreadyExistsException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Account already exist when trying to create.",
        ex.getMessage(),
        HttpStatus.CONFLICT);
  }

  // USED FOR CREATING ACCOUNT
  // ROLE ID DON'T EXIST IN MYSQL
  @ExceptionHandler(InvalidRoleIdException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRoleException(
      InvalidRoleIdException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Role id don't exist", ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  // USED FOR CREATING ACCOUNT
  // CALLED IN AccountValidator
  // ROLE ID AND ROLE NAME DON'T MATCH
  @ExceptionHandler(RoleMismatchException.class)
  public ResponseEntity<ErrorResponse> handleRoleMismatchException(
      RoleMismatchException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Role id and Role name mismatch", ex.getMessage(), HttpStatus.CONFLICT);
  }

  //     FOR ACCOUNT CREATION
  //      CALLED IN AccountValidator
  //     USED TO CHECK WHETHER PAYLOAD IS VALID
  @ExceptionHandler(AccountNotValidException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotValidException(
      AccountNotValidException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Failed to create Account due to invalid parameters",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  // USED FOR FETCHING DATA WITH USER ID
  // FOR CHECKING ACCOUNT EXISTENCE
  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
      AccountNotFoundException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Account don't exist", ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  // FOR LOGGING IN
  // WHEN USERNAME/EMAIL OR PASSWORD IS EMPTY
  @ExceptionHandler(InvalidUserDetailsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidUserDetailsException(
      InvalidUserDetailsException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Invalid login credentials", ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  // USED FOR LOGGING IN
  // IF ACCOUNT PROVIDES WRONG PASSWORD
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(
      BadCredentialsException ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Failed to login due to wrong credentials",
        ex.getMessage(),
        HttpStatus.UNAUTHORIZED);
  }

  // UNAUTHORIZED MANAGEMENT
  // USED TO BLOCK OUT USERS TRYING TO ACCESS OTHER ENDPOINTS NOT BELONGING TO THEIR ROLE
  @ExceptionHandler(UnauthorizedManagement.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedManagementException(
      UnauthorizedManagement ex) {
    return ErrorResponseTemplate.buildResponseError(
        "Unauthorised Management", ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }
}

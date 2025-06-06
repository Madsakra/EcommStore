package com.example.product_store.security.controller;

import com.example.product_store.security.model.Account;
import com.example.product_store.security.service.CreateNewAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateAccountController {

  private final CreateNewAccountService createNewAccountService;

  public CreateAccountController(CreateNewAccountService createNewAccountService) {
    this.createNewAccountService = createNewAccountService;
  }

  @PostMapping("/createAccount")
  public ResponseEntity<String> createNewUser(@RequestBody Account account) {
    String response = createNewAccountService.execute(account);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}

package com.example.product_store.security;

import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.security.model.Account;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

  // USED ONLY WHEN CREATING ACCOUNT
  public void execute(Account account) {

    // CHECKING FOR EMPTY FIELDS
    if (StringUtils.isEmpty(account.getEmail())) {
      throw new AccountNotValidException("Account does not have email");
    }

    if (StringUtils.isEmpty(account.getUserName())) {
      throw new AccountNotValidException("Account does not have user name or malformed payload");
    }

    if (StringUtils.isEmpty(account.getPassword())) {
      throw new AccountNotValidException("Password is not blank!");
    }

    // REGEX CHECK EMAIL
    if (!account.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new AccountNotValidException("Invalid email format");
    }

    // REGEX CHECK PASSWORD
    if (!account.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,}$")) {
      throw new AccountNotValidException("Password must be at least 8 characters and include uppercase, lowercase, number, and special character");
    }

    // CHECK USERNAME
    if (!account.getUserName().matches("^[a-zA-Z0-9_]{3,20}$")) {
      throw new AccountNotValidException("Username must be alphanumeric and 3–20 characters long");
    }

    // CHECK WHETHER ACCOUNT HAS ANY ROLES
    if (account.getRoles().isEmpty()){
      throw new AccountNotValidException("Account does not have any role(s).");
    }

  }
}

package com.example.product_store.authentication;

import com.example.product_store.authentication.dto.AccountRequestDTO;
import com.example.product_store.authentication.errors.AccountNotValidException;
import com.example.product_store.authentication.errors.InvalidRoleIdException;
import com.example.product_store.authentication.errors.RoleMismatchException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.repositories.RoleRepository;
import io.micrometer.common.util.StringUtils;

import java.util.Set;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

  private final RoleRepository roleRepository;

  public AccountValidator(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  // USED ONLY WHEN CREATING ACCOUNT
  public void execute(AccountRequestDTO account, Logger logger) {

    // CHECK WHETHER ACCOUNT HAS ANY ROLES
    if (account.getRoles().isEmpty()) {
      logger.warn("Account does not have any role, throwing AccountNotValidException");
      throw new AccountNotValidException("Account does not have any role(s).");
    }

    // CHECKING FOR EMPTY FIELDS
    // CHECK IF EMAIL IS EMPTY
    if (StringUtils.isEmpty(account.getEmail())) {
      logger.warn("Email is not in client payload, throwing AccountNotValidException");
      throw new AccountNotValidException("Email should not be blank");
    }

    // NO USERNAME
    if (StringUtils.isEmpty(account.getUsername())) {
      logger.warn("Username is not in client payload, throwing AccountNotValidException");
      throw new AccountNotValidException("Account does not have username");
    }

    // NO PASSWORD
    if (StringUtils.isEmpty(account.getPassword())) {
      logger.warn("Password is not in client payload, throwing AccountNotValidException");
      throw new AccountNotValidException("Password should not be blank!");
    }

    // REGEX CHECK EMAIL
    if (!account.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      logger.warn("Email format is invalid, throwing AccountNotValidException");
      throw new AccountNotValidException("Invalid email format");
    }

    // REGEX CHECK PASSWORD
    if (!account
        .getPassword()
        .matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
      logger.warn("Password don't match required pattern, throwing AccountNotValidException");
      throw new AccountNotValidException(
          "Password must be at least 8 characters and include uppercase, lowercase, number, and"
              + " special character");
    }

    // CHECK USERNAME
    if (!account.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
      logger.warn("Username too short, throwing AccountNotValidException");
      throw new AccountNotValidException("Username must be alphanumeric and 3–20 characters long");
    }



    logger.info("Account payload fully validated");

  }
}

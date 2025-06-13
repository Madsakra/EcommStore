package com.example.product_store.security;

import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.security.model.Account;
import com.example.product_store.security.model.Role;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

  private final RoleRepository roleRepository;

  public AccountValidator(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  // USED ONLY WHEN CREATING ACCOUNT
  public void execute(Account account) {

    // CHECK WHETHER ACCOUNT HAS ANY ROLES
    if (account.getRoles().isEmpty()) {
      throw new AccountNotValidException("Account does not have any role(s).");
    }

    List<String> roleIds = account.getRoles().stream().map(Role::getRoleId).toList();

    // CHECKING FOR EMPTY FIELDS
    if (StringUtils.isEmpty(account.getEmail())) {
      throw new AccountNotValidException("Email should not be blank");
    }

    // NO USERNAME
    if (StringUtils.isEmpty(account.getUserName())) {
      throw new AccountNotValidException("Account does not have username");
    }

    // NO PASSWORD
    if (StringUtils.isEmpty(account.getPassword())) {
      throw new AccountNotValidException("Password should not be blank!");
    }

    // REGEX CHECK EMAIL
    if (!account.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new AccountNotValidException("Invalid email format");
    }

    // REGEX CHECK PASSWORD
    if (!account
        .getPassword()
        .matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
      throw new AccountNotValidException(
          "Password must be at least 8 characters and include uppercase, lowercase, number, and"
              + " special character");
    }

    // CHECK USERNAME
    if (!account.getUserName().matches("^[a-zA-Z0-9_]{3,20}$")) {
      throw new AccountNotValidException("Username must be alphanumeric and 3–20 characters long");
    }

    // CHECK IF ROLE ID IS VALID
    for (String roleId : roleIds) {
      if (!roleRepository.existsById(roleId)) {
        throw new AccountNotValidException(
            "Role provided does not exist. Please check the role ID in the payload");
      }
    }
  }
}

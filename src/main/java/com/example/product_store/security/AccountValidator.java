package com.example.product_store.security;

import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.security.model.Account;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

  private final AccountRepository accountRepository;

  public AccountValidator(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public void execute(Account account) {
    if (StringUtils.isEmpty(account.getEmail())) {
      throw new AccountNotValidException("Account does not have email");
    }

    if (StringUtils.isEmpty(account.getUserName())) {
      throw new AccountNotValidException("Account does not have user name or malformed payload");
    }

    if (StringUtils.isEmpty(account.getPassword())) {
      throw new AccountNotValidException("Password is not valid!");
    }
  }
}

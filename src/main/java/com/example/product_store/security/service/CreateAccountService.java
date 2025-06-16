package com.example.product_store.security.service;

import com.example.product_store.Command;
import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.AccountValidator;
import com.example.product_store.security.errors.AccountAlreadyExistsException;
import com.example.product_store.security.model.Account;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountService implements Command<Account, String> {

  private final AccountRepository accountRepository;
  private final PasswordEncoder encoder;
  private final AccountValidator accountValidator;

  public CreateAccountService(
      AccountRepository accountRepository,
      PasswordEncoder passwordEncoder,
      AccountValidator accountValidator) {
    this.accountRepository = accountRepository;
    this.encoder = passwordEncoder;
    this.accountValidator = accountValidator;
  }

  @Override
  public String execute(Account account) {
    Optional<Account> optionalAccount = accountRepository.findByUserName(account.getUserName());
    if (optionalAccount.isPresent()) {
      throw new AccountAlreadyExistsException("An account with this username already exists.");
    }

    // ✅ Validate raw user input first
    accountValidator.execute(account);

    // ✅ Then encode the password after validation passes
    Account newAccount =
        new Account(
            null,
            account.getUserName(),
            account.getEmail(),
            encoder.encode(account.getPassword()),
            account.getRoles());

    accountRepository.save(newAccount);
    return "User Created Successfully";
  }
}

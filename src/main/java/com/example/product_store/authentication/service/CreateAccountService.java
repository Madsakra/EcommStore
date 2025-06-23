package com.example.product_store.authentication.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.authentication.AccountValidator;
import com.example.product_store.authentication.dto.AccountDTO;
import com.example.product_store.authentication.errors.AccountAlreadyExistsException;
import com.example.product_store.authentication.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountService implements Command<Account, AccountDTO> {

  private final AccountRepository accountRepository;
  private final PasswordEncoder encoder;
  private final AccountValidator accountValidator;
  private static Logger logger = LoggerFactory.getLogger(CreateAccountService.class);


  public CreateAccountService(
      AccountRepository accountRepository,
      PasswordEncoder passwordEncoder,
      AccountValidator accountValidator) {
    this.accountRepository = accountRepository;
    this.encoder = passwordEncoder;
    this.accountValidator = accountValidator;
  }

  @Override
  public AccountDTO execute(Account account) {

    // CHECK ACCOUNT REPOSITORY
    // USE USERNAME / EMAIL IN PAYLOAD
    // TO CHECK WHETHER ACCOUNT EXIST
    boolean accountExist = accountRepository.existsByUserNameOrEmail(account.getUserName(),account.getEmail());



    // IF ACCOUNT EXIST, THROW ERROR
    // DISPLAY ERROR TO CLIENT THROUGH GLOBAL EXCEPTION HANDLER
    if (accountExist) {
      logger.warn("Username / email already exist in db, throwing AccountAlreadyExist error");
      throw new AccountAlreadyExistsException("An account with this username or email already exists.");
    }

    // ✅ Validate raw user input first
    // WILL VALIDATE PAYLOAD FROM USER ACCOUNT VALIDATOR CLASS
    accountValidator.execute(account,logger);

    // ✅ Then encode the password after validation passes
    Account newAccount =
        new Account(
            null,
            account.getUserName(),
            account.getEmail(),
            encoder.encode(account.getPassword()),
            account.getRoles());

    accountRepository.save(newAccount);

    logger.info("Account saved :{}",newAccount);

    return new AccountDTO(newAccount);
  }
}

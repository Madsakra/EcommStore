package com.example.product_store.authentication.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.AccountValidator;
import com.example.product_store.authentication.RoleValidatorUtil;
import com.example.product_store.authentication.dto.AccountDTO;
import com.example.product_store.authentication.dto.AccountRequestDTO;
import com.example.product_store.authentication.errors.AccountAlreadyExistsException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.repositories.AccountRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountService implements Command<AccountRequestDTO, AccountDTO> {

  private final AccountRepository accountRepository;
  private final PasswordEncoder encoder;
  private final AccountValidator accountValidator;
  private static Logger logger = LoggerFactory.getLogger(CreateAccountService.class);

  private final RoleValidatorUtil roleValidatorUtil;

  public CreateAccountService(
      AccountRepository accountRepository,
      PasswordEncoder passwordEncoder,
      AccountValidator accountValidator,
      RoleValidatorUtil roleValidatorUtil) {
    this.accountRepository = accountRepository;
    this.encoder = passwordEncoder;
    this.accountValidator = accountValidator;
    this.roleValidatorUtil = roleValidatorUtil;
  }

  @Override
  public AccountDTO execute(AccountRequestDTO account) {

    // CHECK ACCOUNT REPOSITORY
    // USE USERNAME / EMAIL IN PAYLOAD
    // TO CHECK WHETHER ACCOUNT EXIST
    boolean accountExist =
        accountRepository.existsByUserNameOrEmail(
            account.getUsername(), account.getEmail());

    // IF ACCOUNT EXIST, THROW ERROR
    // DISPLAY ERROR TO CLIENT THROUGH GLOBAL EXCEPTION HANDLER
    if (accountExist) {
      logger.warn(
          "Username / email already exist in db, throwing AccountAlreadyExist error");
      throw new AccountAlreadyExistsException(
          "An account with this username or email already exists.");
    }
    Set<Role> validatedRoles = roleValidatorUtil.validateRoles(account.getRoles());

    // ✅ Validate raw user input first
    // WILL VALIDATE PAYLOAD FROM USER ACCOUNT VALIDATOR CLASS
    accountValidator.execute(account, logger);

    // ✅ Then encode the password after validation passes
    Account newAccount =
        new Account(
            null,
            account.getUsername(),
            account.getEmail(),
            encoder.encode(account.getPassword()),
            validatedRoles);

    accountRepository.save(newAccount);

    logger.info("Account saved :{}", newAccount);

    return new AccountDTO(newAccount);
  }
}

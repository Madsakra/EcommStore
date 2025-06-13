package com.example.product_store.security.service;

import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.model.Account;
import com.example.product_store.security.model.MyUserDetails;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

  private final AccountRepository accountRepository;
  private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

  public LoginService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  // USING CUSTOM JPA TO SEARCH FOR USER ACCOUNT BASED ON
  // EMAIL AND PASSWORD
  @Override
  public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {

    Optional<Account> optionalAccount =
        accountRepository.findUserByEmailOrUserName(loginIdentifier);
    if (optionalAccount.isEmpty()) {
      logger.warn("LoginService: User not found with identifier: {}", loginIdentifier);
      throw new UsernameNotFoundException("Account does not exist!");
    }
    Account account = optionalAccount.get();
    logger.info("LoginService: User found with ID: {}", account.getId());
    return new MyUserDetails(account);
  }
}

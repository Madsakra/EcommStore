package com.example.product_store.security.service;

import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.model.Account;
import com.example.product_store.security.model.MyUserDetails;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

  private final AccountRepository accountRepository;

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
      throw new UsernameNotFoundException("Account does not exist!");
    }
    Account account = optionalAccount.get();



    return new MyUserDetails(account);
  }
}

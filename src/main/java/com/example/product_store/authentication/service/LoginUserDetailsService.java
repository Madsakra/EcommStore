package com.example.product_store.authentication.service;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.jwt.MyUserDetails;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;

@Service
public class LoginUserDetailsService implements UserDetailsService {

    // MEANT TO CHECK IF THE USERNAME / EMAIL EXIST IN DB FIRST
    // TRIGGERED WHEN LOGGING IN THROUGH AUTHENTICATION MANAGER
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoginUserDetailsService.class);

    public LoginUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        Optional<Account> optionalAccount =
                accountRepository.findUserByEmailOrUserName(loginIdentifier);
        if (optionalAccount.isEmpty()) {
            logger.warn("LoginService: User not found with identifier: {}", loginIdentifier);
            throw new AccountNotFoundException("Account does not exist!");
        }
        Account account = optionalAccount.get();
        logger.info("LoginService: User found with ID: {}", account.getId());
        return new MyUserDetails(account);
    }

}

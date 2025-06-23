package com.example.product_store.authentication.service;

import com.example.product_store.authentication.jwt.MyUserDetails;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;

@Service
public class UserDetailsService implements UserDetailsService  {

    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public LoginService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // USING CUSTOM JPA TO SEARCH FOR USER ACCOUNT BASED ON
    // EMAIL AND PASSWORD
    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {

        if (loginIdentifier==null){
            throw new UsernameNotFoundException("User name cannot be null");
        }

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

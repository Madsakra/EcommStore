package com.example.product_store.authentication.service;

import com.example.product_store.authentication.dto.UserDetailsDTO;
import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoadCachedUserService {

    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoadCachedUserService.class);

    public LoadCachedUserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // WILL RETURN WITH CACHE VALUE IF IT IS AVAILABLE
   @Cacheable(value = "userDetailsCache", key = "#loginIdentifier")
    public UserDetailsDTO execute(String loginIdentifier) {
        // IF NO CACHE
       // GO DB AND FETCH
        Optional<Account> optionalAccount =
                accountRepository.findUserByEmailOrUserName(loginIdentifier);

        // IF NO ACCOUNT IS FOUND IN DB
        if (optionalAccount.isEmpty()) {
            logger.warn("LoginService: Account with identifier: {} not found", loginIdentifier);
            throw new AccountNotFoundException("Account does not exist!");
        }

        // GET THE ACCOUNT IN OPTIONAL
        Account account = optionalAccount.get();

        // CREATE A SET FOR ROLES
       // USE SET TO PREVENT DUPLICATES
        Set<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        // RETURN UserDetailsDTO
        return new UserDetailsDTO(account.getId(),account.getUserName(), account.getPassword(), roles);
    }
}

package com.example.product_store.authentication.service;

import com.example.product_store.authentication.dto.CachedUserDetailsDTO;
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

    // FETCH THE ACCOUNT BACK
    // FIRST FROM CACHE IF AVAILABLE
   @Cacheable(value = "userDetailsCache", key = "#loginIdentifier")
    public CachedUserDetailsDTO execute(String loginIdentifier) {
        Optional<Account> optionalAccount =
                accountRepository.findUserByEmailOrUserName(loginIdentifier);

        if (optionalAccount.isEmpty()) {
            logger.warn("LoginService: Account with identifier: {} not found", loginIdentifier);
            throw new AccountNotFoundException("Account does not exist!");
        }

        Account account = optionalAccount.get();

        Set<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());


        return new CachedUserDetailsDTO(account.getId(),account.getUserName(), account.getPassword(), roles);
    }
}

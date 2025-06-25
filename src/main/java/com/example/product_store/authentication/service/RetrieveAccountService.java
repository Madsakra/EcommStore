package com.example.product_store.authentication.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RetrieveAccountService implements Command<Void, Account> {

    // Microservice to help fetch account information (UUID specifically) based on jwt
    // returns entire account
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(RetrieveAccountService.class);

    public RetrieveAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account execute(Void input) {String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Account id found: {}", userId);

        Optional<Account> account = accountRepository.findByIdForUpdate(userId);

        if (account.isEmpty()){
                logger.warn("RetrieveAccountService: unable to identify account with ID: {}",userId);
                throw new AccountNotFoundException("Account with id: "+userId+" not found." );
        }

        return account.get();
    }
}

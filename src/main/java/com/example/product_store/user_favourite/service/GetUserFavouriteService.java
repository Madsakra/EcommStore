package com.example.product_store.user_favourite.service;
import com.example.product_store.QueryBinder;
import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.security.model.Account;
import com.example.product_store.user_favourite.dto.UserFavouriteDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserFavouriteService implements QueryBinder<Void, UserFavouriteDTO> {

    private final AccountRepository accountRepository;

    public GetUserFavouriteService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    @Cacheable(cacheNames = "getUserFavorites", key = "'userFavorites'")
    public UserFavouriteDTO execute(Void input){

        // 1. use the jwt to parse the current user id
        String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Use the current user id to fetch his account
        Optional<Account> accountOptional = accountRepository.findById(jti);
        Account account = accountOptional.orElseThrow(() -> new AccountNotValidException("Account not found"));
        return new UserFavouriteDTO(account);
    }


}

package com.example.product_store.user_favourite.service;

import com.example.product_store.QueryBinder;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.user_favourite.dto.UserFavouriteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetUserFavouriteService implements QueryBinder<Void, UserFavouriteDTO> {

  private final AccountRepository accountRepository;
  private static final Logger logger = LoggerFactory.getLogger(GetUserFavouriteService.class);

  public GetUserFavouriteService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  @Cacheable(cacheNames = "getUserFavorites", key = "'userFavorites'")
  public UserFavouriteDTO execute(Void input) {

    // 1. use the jwt to parse the current user id
    String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    logger.info("User ID {} found in GetUserFavouriteService", jti);

    // 2. Use the current user id to fetch his account
    Account account =
        accountRepository.findById(jti).orElseThrow(() -> new AccountNotFoundException("Account not found"));

    logger.info("Account {} retrieved for in GetUserFavouriteService", jti);

    return new UserFavouriteDTO(account);
  }
}

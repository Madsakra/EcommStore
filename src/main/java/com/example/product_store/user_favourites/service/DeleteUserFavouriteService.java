package com.example.product_store.user_favourites.service;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserFavouriteService {
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private static final Logger logger =
      LoggerFactory.getLogger(DeleteUserFavouriteService.class);

  public DeleteUserFavouriteService(
      AccountRepository accountRepository, ProductRepository productRepository) {
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  @CacheEvict(cacheNames = "getUserFavorites", key = "'userFavorites'")
  public Void execute(String jti, String id) {

    // 1. Use the current user id to fetch his account
    Account account =
        accountRepository
            .findById(jti)
            .orElseThrow(
                () -> new AccountNotFoundException("Account not found with current JWT"));

    logger.info(
        "Account {} exist and ready to delete favourites in DeleteUserFavouriteService",
        jti);

    // 2. Use the id of the product, go to the repo and find for it.
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ProductNotFoundException("Product not found with the given id"));

    logger.info(
        "Product {} exist and ready to be added to favourites in"
            + " DeleteUserFavouriteService",
        id);
    // 3. Save the product to the account repository
    account.getFavouriteProducts().remove(product);
    // 4. Save the updated account
    accountRepository.save(account);
    logger.info("Deleted product to favourite list, returning no content");

    return null;
  }
}

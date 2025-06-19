package com.example.product_store.user_favourite.service;

import com.example.product_store.Command;
import com.example.product_store.authentication.errors.AccountNotValidException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserFavouriteService implements Command<String, Void> {
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private static final Logger logger = LoggerFactory.getLogger(DeleteUserFavouriteService.class);

  public DeleteUserFavouriteService(AccountRepository accountRepository, ProductRepository productRepository) {
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
  }

  @Override
  @Transactional
  @CacheEvict(cacheNames = "getUserFavorites", key = "'userFavorites'")
  public Void execute(String id) {

    // 1. Frontend will enter the id of the client in the endpoint path variable
    // 2. Check the product repository if the product exist
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotValidException("Product has an invalid id"));

    logger.info("Product {} exist and ready to be added to favourites in DeleteUserFavouriteService", id);

    // 3. use the jwt to parse the current user id
    String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // 4. Use the current user id to fetch his account
    Account account =
        accountRepository.findById(jti).orElseThrow(() -> new AccountNotValidException("Account not found"));

    logger.info("Account {} exist and ready to delete favourites in DeleteUserFavouriteService", jti);

    // 5. Save the product to the account repository
    account.getFavouriteProducts().remove(product);

    // 6. Save the updated account
    accountRepository.save(account);

    logger.info("Deleted product to favourite list, returning no content");

    return null;
  }
}

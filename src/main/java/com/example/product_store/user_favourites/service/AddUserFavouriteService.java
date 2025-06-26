package com.example.product_store.user_favourites.service;

import com.example.product_store.Command;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.user_favourites.dto.UserFavouriteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddUserFavouriteService implements Command<String, UserFavouriteDTO> {

  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private static final Logger logger = LoggerFactory.getLogger(AddUserFavouriteService.class);

  public AddUserFavouriteService(AccountRepository accountRepository, ProductRepository productRepository) {
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
  }

  // TRANSACTIONAL TO PREVENT PARTIAL SAVES
  @Override
  @Transactional
  @CacheEvict(cacheNames = "getUserFavorites", key = "'userFavorites'")
  public UserFavouriteDTO execute(String id) {

    // 1. Frontend will enter the id of the client in the endpoint path variable
    // 2. Check the product repository if the product exist
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with the given id"));

    logger.info("Product {} exist and ready to be added to favourites in AddUserFavouriteService", id);

    // 3. use the jwt to parse the current user id
    String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // 4. Use the current user id to fetch his account
    Account account =
        accountRepository.findById(jti).orElseThrow(() -> new AccountNotFoundException("Account not found with current JWT"));

    logger.info("Account {} exist and ready to add favourites in AddUserFavouriteService", jti);

    // 5. Save the product to the account repository
    account.getFavouriteProducts().add(product);

    // 6. Save the updated account
    accountRepository.save(account);

    logger.info("Added product to favourite list, returning user favourite DTO from AddUserFavouriteService");

    // 7. return the updated DTO
    return new UserFavouriteDTO(account);
  }
}

package com.example.product_store.user_favourites.service;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.user_favourites.dto.UserFavouriteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddUserFavouriteService {

  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private static final Logger logger = LoggerFactory.getLogger(AddUserFavouriteService.class);

  public AddUserFavouriteService(AccountRepository accountRepository, ProductRepository productRepository) {
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
  }

  // TRANSACTIONAL TO PREVENT PARTIAL SAVES
  @Transactional
  @CacheEvict(cacheNames = "getUserFavorites", key = "'userFavorites'")
  public UserFavouriteDTO execute(String jti,String id) {

    // 1. Use the current user id to fetch his account
    Account account =
            accountRepository.findById(jti).orElseThrow(() -> new AccountNotFoundException("Account not found with current JWT"));
    // 2. Frontend will enter the id of the client in the endpoint path variable
    // 3. Check the product repository if the product exist
    logger.info("Account {} exist and ready to add favourites in AddUserFavouriteService", jti);
    Product product =
        productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    logger.info("Product {} exist and ready to be added to favourites in AddUserFavouriteService", id);
    // 4. Save the product to the account repository
    account.getFavouriteProducts().add(product);
    // 5. Save the updated account
    accountRepository.save(account);
    logger.info("Added product to favourite list, returning user favourite DTO from AddUserFavouriteService");
    // 6. return the updated DTO
    return new UserFavouriteDTO(account);
  }
}

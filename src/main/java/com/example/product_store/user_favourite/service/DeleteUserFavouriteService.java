package com.example.product_store.user_favourite.service;

import com.example.product_store.Command;
import com.example.product_store.security.AccountRepository;
import com.example.product_store.security.errors.AccountNotValidException;
import com.example.product_store.security.model.Account;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.user_favourite.dto.UserFavouriteDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteUserFavouriteService implements Command<String, Void> {
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final GetUserFavouriteService getUserFavouriteService;

    public DeleteUserFavouriteService(AccountRepository accountRepository, ProductRepository productRepository, GetUserFavouriteService getUserFavouriteService) {
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
        this.getUserFavouriteService = getUserFavouriteService;
    }

    @Override
    @CacheEvict(cacheNames = "getUserFavorites", key = "'userFavorites'")
    public Void execute(String id){

        // 1. Frontend will enter the id of the client in the endpoint path variable
        // 2. Check the product repository if the product exist
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()){
            throw new ProductNotValidException("Product has an invalid id");
        }

        Product productTarget = productOptional.get();

        // 3. use the jwt to parse the current user id
        String jti = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 4. Use the current user id to fetch his account
        Optional<Account> accountOptional = accountRepository.findById(jti);
        Account account = accountOptional.orElseThrow(() -> new AccountNotValidException("Account not found"));

        // 5. Save the product to the account repository
        account.getFavouriteProducts().remove(productTarget);

        // 6. Save the updated account
        accountRepository.save(account);
        return null;
    }



}

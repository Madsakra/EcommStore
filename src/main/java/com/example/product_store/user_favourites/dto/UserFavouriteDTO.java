package com.example.product_store.user_favourites.dto;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.store.product.dto.ProductDTO;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class UserFavouriteDTO implements Serializable {

  private String id;
  private Set<ProductDTO> favouriteProducts = new HashSet<>();

  // CACHEABLE DTO
  // ONLY RETURN FAVOURITES SET THROUGH ACCOUNT ENTITY
  // PREVENT ACCOUNT ENTITY FROM PASSING OVER EVERYTHING
  // ALSO USE PRODUCT DTO FOR PREVENTING LEAKS
  public UserFavouriteDTO(Account account) {
    this.id = account.getId();
    this.favouriteProducts = account.getFavouriteProducts().stream().map(ProductDTO::new).collect(Collectors.toSet());
  }
}

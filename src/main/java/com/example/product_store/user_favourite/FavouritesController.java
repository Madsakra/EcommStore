package com.example.product_store.user_favourite;

import com.example.product_store.user_favourite.dto.FavoriteRequestDTO;
import com.example.product_store.user_favourite.dto.UserFavouriteDTO;
import com.example.product_store.user_favourite.service.AddUserFavouriteService;
import com.example.product_store.user_favourite.service.DeleteUserFavouriteService;
import com.example.product_store.user_favourite.service.GetUserFavouriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class FavouritesController {

  private final GetUserFavouriteService getUserFavouriteService;
  private final AddUserFavouriteService addUserFavouriteService;
  private final DeleteUserFavouriteService deleteUserFavouriteService;

  public FavouritesController(
      GetUserFavouriteService getUserFavouriteService,
      AddUserFavouriteService addUserFavouriteService,
      DeleteUserFavouriteService deleteUserFavouriteService) {
    this.getUserFavouriteService = getUserFavouriteService;
    this.addUserFavouriteService = addUserFavouriteService;
    this.deleteUserFavouriteService = deleteUserFavouriteService;
  }

  // GET ALL FAVOURITES
  @GetMapping("/user/store/favorites")
  public ResponseEntity<UserFavouriteDTO> getUserFavorites() {
    UserFavouriteDTO userFavourites = getUserFavouriteService.execute(null);
    return ResponseEntity.status(HttpStatus.OK).body(userFavourites);
  }

  // ADD FAVOURITES
  @PostMapping("/user/store/add-favorites")
  public ResponseEntity<UserFavouriteDTO> addUserFavorites(
      @RequestBody FavoriteRequestDTO favoriteRequestDTO) {
    UserFavouriteDTO updatedUserFavourites =
        addUserFavouriteService.execute(favoriteRequestDTO.getId());
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserFavourites);
  }

  // DELETE FAVOURITES
  @DeleteMapping("/user/store/delete-favorites")
  public ResponseEntity<UserFavouriteDTO> deleteUserFavorites(
      @RequestBody FavoriteRequestDTO favoriteRequestDTO) {
        deleteUserFavouriteService.execute(favoriteRequestDTO.getId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}

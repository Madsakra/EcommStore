package com.example.product_store.user_favourite;

import com.example.product_store.user_favourite.dto.UserFavouriteDTO;
import com.example.product_store.user_favourite.service.AddUserFavouriteService;
import com.example.product_store.user_favourite.service.DeleteUserFavouriteService;
import com.example.product_store.user_favourite.service.GetUserFavouriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
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
  @GetMapping("/favorites")
  public ResponseEntity<UserFavouriteDTO> getUserFavorites() {
    UserFavouriteDTO userFavourites = getUserFavouriteService.execute(null);
    return ResponseEntity.status(HttpStatus.OK).body(userFavourites);
  }

  // ADD FAVOURITES
  @PostMapping("/favorites/{id}")
  public ResponseEntity<UserFavouriteDTO> addUserFavorites(
          @PathVariable("id") String id) {
    UserFavouriteDTO updatedUserFavourites =
        addUserFavouriteService.execute(id);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserFavourites);
  }

  // DELETE FAVOURITES
  @DeleteMapping("/favorites/{id}")
  public ResponseEntity<UserFavouriteDTO> deleteUserFavorites(
          @PathVariable("id") String id) {
        deleteUserFavouriteService.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}

package com.example.product_store.user_favourites;

import com.example.product_store.user_favourites.dto.UserFavouriteDTO;
import com.example.product_store.user_favourites.service.AddUserFavouriteService;
import com.example.product_store.user_favourites.service.DeleteUserFavouriteService;
import com.example.product_store.user_favourites.service.GetUserFavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Product Favourites Management",
    description = "APIs for managing user's favourites. Usable by user accounts only.")
@RestController
@RequestMapping("/user")
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
  @Operation(
      summary = "Get All User Favourites",
      description =
          "Get all the favourites of the current authenticated User. Only usable by"
              + " users.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully created",
            content =
                @Content(schema = @Schema(implementation = UserFavouriteDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found.",
            content = @Content(schema = @Schema())),
      })
  @GetMapping("/favorites")
  public ResponseEntity<UserFavouriteDTO> getUserFavorites() {
    UserFavouriteDTO userFavourites = getUserFavouriteService.execute(null);
    return ResponseEntity.status(HttpStatus.OK).body(userFavourites);
  }

  // ADD FAVOURITES
  @Operation(
      summary = "Add a product to user favourite",
      description = "Add a product to user favourite list. Only usable by users.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Favourite Successfully added",
            content =
                @Content(schema = @Schema(implementation = UserFavouriteDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found.",
            content = @Content(schema = @Schema())),
      })
  @PostMapping("/favorites/{id}")
  public ResponseEntity<UserFavouriteDTO> addUserFavorites(
      @PathVariable("id") String id) {
    UserFavouriteDTO updatedUserFavourites = addUserFavouriteService.execute(id);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserFavourites);
  }

  // DELETE FAVOURITES
  @Operation(
      summary = "Remove a user favourite",
      description =
          "Remove a user favourite from the favourites list. Only usable by users.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Favourite successfully removed",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found.",
            content = @Content(schema = @Schema())),
      })
  @DeleteMapping("/favorites/{id}")
  public ResponseEntity<UserFavouriteDTO> deleteUserFavorites(
      @PathVariable("id") String id) {
    deleteUserFavouriteService.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}

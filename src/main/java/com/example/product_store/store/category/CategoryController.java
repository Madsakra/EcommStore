package com.example.product_store.store.category;

import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.dto.CategoryRequestDTO;
import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Category Management",
    description = "APIs for managing category of products. Usable by super admins only")
@RestController
@RequestMapping("/superAdmin")
public class CategoryController {

  private final GetCategoriesService getCategoriesService;
  private final CreateCategoryService createCategoryService;
  private final DeleteCategoryService deleteCategoryService;
  public final SearchCategoryByName searchCategoryByName;
  private final UpdateCategoryService updateCategoryService;

  public CategoryController(
      GetCategoriesService getCategoriesService,
      CreateCategoryService createCategoryService,
      DeleteCategoryService deleteCategoryService,
      SearchCategoryByName searchCategoryByName,
      UpdateCategoryService updateCategoryService) {
    this.getCategoriesService = getCategoriesService;
    this.createCategoryService = createCategoryService;
    this.deleteCategoryService = deleteCategoryService;
    this.searchCategoryByName = searchCategoryByName;
    this.updateCategoryService = updateCategoryService;
  }

  // GET ALL PRODUCTS
  @Operation(
      summary = "Get all categories",
      description = "Get all categories in the current database. Usable by admins only",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories Found",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CategoryDTO.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management.",
            content = @Content(schema = @Schema())),
      })
  @GetMapping("/categories")
  public ResponseEntity<List<CategoryDTO>> getCategories() {
    List<CategoryDTO> categoryDTOS = getCategoriesService.execute(null);
    return ResponseEntity.status(HttpStatus.OK).body(categoryDTOS);
  }

  @Operation(
      summary = "Search for category",
      description = "Search for category by its name",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories Found",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CategoryDTO.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management.",
            content = @Content(schema = @Schema())),
      })
  // SEARCH BY CATEGORY NAME
  @GetMapping("/categories/{categoryName}")
  public ResponseEntity<List<CategoryDTO>> searchCategory(
      @PathVariable("categoryName") String categoryName) {
    List<CategoryDTO> categoryDTOS = searchCategoryByName.execute(categoryName);
    return ResponseEntity.status(HttpStatus.OK).body(categoryDTOS);
  }

  // CREATE NEW CATEGORY
  @Operation(
      summary = "Create a category",
      description = "Create a category for products to be labeled. Usable by admins only",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories Created",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management.",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Category already exist.",
            content = @Content(schema = @Schema())),
      })
  @PostMapping("/categories")
  public ResponseEntity<CategoryDTO> createCategory(
      @RequestBody CategoryRequestDTO categoryRequestDTO) {
    // convert from request to Category Entity
    Category category = new Category(categoryRequestDTO);
    CategoryDTO categoryDTO = createCategoryService.execute(category);
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
  }

  // DELETE CATEGORY
  @Operation(
      summary = "Delete a category",
      description = "Delete a category to remove redundancy. Usable by admins only",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories Deleted",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management.",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Category don't exist.",
            content = @Content(schema = @Schema())),
      })
  @DeleteMapping("/categories/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable("id") String id) {
    deleteCategoryService.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // UPDATE Categories
  @Operation(
      summary = "Update a category.",
      description = "Update a category name. Usable by admins only",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories Updated",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management.",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Category don't exist.",
            content = @Content(schema = @Schema())),
      })
  @PutMapping("/categories/{id}")
  public ResponseEntity<CategoryDTO> updateCategoryDTO(
      @PathVariable("id") String id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
    Category category = new Category(categoryRequestDTO);
    CategoryDTO categoryDTO =
        updateCategoryService.execute(new UpdateCategoryCommand(id, category));
    return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
  }
}

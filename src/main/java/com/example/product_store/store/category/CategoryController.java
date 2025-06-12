package com.example.product_store.store.category;

import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.category.dto.CategoryDTO;
import com.example.product_store.store.category.service.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")

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
  @GetMapping("/getAll")
  public ResponseEntity<List<CategoryDTO>> getCategories() {
    List<CategoryDTO> categoryDTOS = getCategoriesService.execute(null);
    return ResponseEntity.status(HttpStatus.OK).body(categoryDTOS);
  }

  // SEARCH BY CATEGORY NAME
  @GetMapping("/search/{categoryName}")
  public ResponseEntity<List<CategoryDTO>> getCategoriesByName(@PathVariable String categoryName) {
    List<CategoryDTO> categoryDTOS = searchCategoryByName.execute(categoryName);
    return ResponseEntity.status(HttpStatus.OK).body(categoryDTOS);
  }

  // CREATE NEW PRODUCT
  @PostMapping("/create")
  public ResponseEntity<CategoryDTO> createCategories(@RequestBody Category category) {
    CategoryDTO categoryDTO = createCategoryService.execute(category);
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
  }

  // DELETE PRODUCT
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
    deleteCategoryService.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // UPDATE Categories
  @PutMapping("/update/{id}")
  public ResponseEntity<CategoryDTO> updateCategoryDTO(@PathVariable String id, @RequestBody Category category) {
    CategoryDTO categoryDTO = updateCategoryService.execute(new UpdateCategoryCommand(id, category));
    return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
  }
}

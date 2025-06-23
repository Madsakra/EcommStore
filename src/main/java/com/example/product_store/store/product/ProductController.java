package com.example.product_store.store.product;

import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.service.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

  private final GetProductsService getProductsService;
  private final CreateProductService createProductService;
  private final DeleteProductService deleteProductService;
  private final SearchProductService searchProductService;
  private final UpdateProductService updateProductService;

  public ProductController(
      GetProductsService getProductsService,
      CreateProductService createProductService,
      DeleteProductService deleteProductService,
      SearchProductService searchProductService,
      UpdateProductService updateProductService) {
    this.getProductsService = getProductsService;
    this.createProductService = createProductService;
    this.deleteProductService = deleteProductService;
    this.searchProductService = searchProductService;
    this.updateProductService = updateProductService;
  }

  // GENERAL ENDPOINT
  // GET ALL PRODUCTS
  @GetMapping("/products")
  public ResponseEntity<List<ProductDTO>> getProducts(
      @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
      @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(name = "categoryIds", required = false) List<String> categoryIds,
      @RequestParam(name="page",defaultValue = "0") int page,
      @RequestParam(name="size",defaultValue = "10") int size
      ) {
    // CHECK IF PAGE NUMBER IS NULL OR NEGATIVE
    if (page < 0){
      throw new InvalidPageRequestException("Page number cannot be negative.");
    }

    // CHECK IF PAGE SIZE IS NEGATIVE
    if (size < 0){
      throw new InvalidPageRequestException("Page size cannot be negative.");
    }

    ProductFilter productFilter = new ProductFilter();
    productFilter.setMinPrice(minPrice);
    productFilter.setMaxPrice(maxPrice);
    productFilter.setCategoryIds(categoryIds);

    List<ProductDTO> products = getProductsService.execute(productFilter, PageRequest.of(page,size));
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  // SEARCH PRODUCT BY TITLE
  @GetMapping("/products/title/{title}")
  public ResponseEntity<List<ProductDTO>> searchProductByTitle(@PathVariable("title") String title) {
    List<ProductDTO> products = searchProductService.execute(title);
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  // SEARCH PRODUCT BY DESCRIPTION
  @GetMapping("/products/description/{description}")
  public ResponseEntity<List<ProductDTO>> searchProductByDescription(@PathVariable("description") String description) {
    List<ProductDTO> products = searchProductService.searchProductByDescription(description);
    return ResponseEntity.ok(products);
  }

  // ONLY FOR ADMINS
  // CREATE NEW PRODUCT
  @PostMapping("/admin/products")
  public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
    ProductDTO productDTO = createProductService.execute(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
  }

  // UPDATE PRODUCT
  // CHALLENGE - > ONLY CAN UPDATE IF PRODUCT BELONGS TO ADMIN
  @PutMapping("/admin/products/{id}")
  public ResponseEntity<ProductDTO> updateProductDTO(@PathVariable("id") String id, @RequestBody Product product) {
    ProductDTO productDTO = updateProductService.execute(new UpdateProductCommand(id, product));
    return ResponseEntity.status(HttpStatus.OK).body(productDTO);
  }

  // DELETE PRODUCT
  // CHALLENGE - > ONLY CAN DELETE IF PRODUCT BELONGS TO ADMIN
  @DeleteMapping("/admin/products/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
    deleteProductService.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}

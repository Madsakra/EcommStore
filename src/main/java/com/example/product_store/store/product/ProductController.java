package com.example.product_store.store.product;

import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.dto.ProductRequestDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
public class ProductController {

  private final GetProductsService getProductsService;
  private final CreateProductService createProductService;
  private final DeleteProductService deleteProductService;
  private final SearchProductService searchProductService;
  private final UpdateProductService updateProductService;
  public final Logger logger = LoggerFactory.getLogger(ProductController.class);

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
//  @Operation(
//          summary = "Get Products",
//          description = "Retrieve all products from the database with optional filters. Usable by both admins and users.",
//          parameters = {
//                  @Parameter(name = "minPrice", description = "Minimum price filter", example = "10.00"),
//                  @Parameter(name = "maxPrice", description = "Maximum price filter", example = "500.00"),
//                  @Parameter(name = "categoryIds", description = "List of category IDs to filter", example = "cat1,cat2"),
//                  @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
//                  @Parameter(name = "size", description = "Number of products per page", example = "10")
//          }
//  )
//  @ApiResponses(value = {
//          @ApiResponse(
//                  responseCode = "200",
//                  description = "Successfully retrieved product list",
//                  content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))
//          ),
//          @ApiResponse(
//                  responseCode = "401",
//                  description = "Unauthorized access (e.g., missing or invalid token)",
//                  content = @Content(schema = @Schema())
//          ),
//          @ApiResponse(
//                  responseCode = "400",
//                  description = "Invalid pagination or filter parameters",
//                  content = @Content(schema = @Schema())
//          ),
//  })
  @GetMapping("/products")
  public ResponseEntity<List<ProductDTO>> getProducts(
      @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
      @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(name = "categoryIds", required = false) List<String> categoryIds,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    // CHECK IF PAGE NUMBER IS NULL OR NEGATIVE
    if (page < 0) {
      throw new InvalidPageRequestException("Page number cannot be negative.");
    }

    // CHECK IF PAGE SIZE IS NEGATIVE
    if (size < 0) {
      throw new InvalidPageRequestException("Page size cannot be negative.");
    }

    ProductFilter productFilter = new ProductFilter();
    productFilter.setMinPrice(minPrice);
    productFilter.setMaxPrice(maxPrice);
    productFilter.setCategoryIds(categoryIds);

    List<ProductDTO> products =
        getProductsService.execute(productFilter, PageRequest.of(page, size));
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  // SEARCH PRODUCT BY TITLE
  // OPTIONAL TO USE DESCRIPTION
//  @Operation(
//          summary = "Search for Products",
//          description = "Search for products from the database with optional filters. Usable by both admins and users.",
//          parameters = {
//                  @Parameter(name="title",required = true,description = "Title to search",example = "Nike"),
//                  @Parameter(name="description",description = "Description to filter",example = "A book"),
//                  @Parameter(name = "minPrice", description = "Minimum price filter", example = "10.00"),
//                  @Parameter(name = "maxPrice", description = "Maximum price filter", example = "500.00"),
//                  @Parameter(name = "categoryIds", description = "List of category IDs to filter", example = "cat1,cat2"),
//                  @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
//                  @Parameter(name = "size", description = "Number of products per page", example = "10")
//          }
//  )
//  @ApiResponses(value = {
//          @ApiResponse(
//                  responseCode = "200",
//                  description = "Successfully retrieved product list",
//                  content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))
//          ),
//          @ApiResponse(
//                  responseCode = "401",
//                  description = "Unauthorized access (e.g., missing or invalid token)",
//                  content = @Content(schema = @Schema())
//          ),
//          @ApiResponse(
//                  responseCode = "400",
//                  description = "Title is null or invalid pagination / filter parameters. ",
//                  content = @Content(schema = @Schema())
//          ),
//  })
  @GetMapping("/products/search")
  public ResponseEntity<List<ProductDTO>> searchProduct(
      @RequestParam(name = "title") String title,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
      @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(name = "categoryIds", required = false) List<String> categoryIds,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    ProductFilter productFilter = new ProductFilter();
    productFilter.setMinPrice(minPrice);
    productFilter.setMaxPrice(maxPrice);
    productFilter.setCategoryIds(categoryIds);
    productFilter.setTitle(title);
    productFilter.setDescription(description);
    List<ProductDTO> products =
        searchProductService.execute(productFilter, PageRequest.of(page, size));
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  // ONLY FOR ADMINS
  // CREATE NEW PRODUCT
  @Operation(
      summary = "Create a product",
      description = "Create an a product for users to order. Only usable by admins")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully created",
            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Failed to create product due to invalid payload.",
            content = @Content(schema = @Schema())),
      })
  @PostMapping("/admin/products")
  public ResponseEntity<ProductDTO> createProduct(
      @RequestBody ProductRequestDTO requestDTO) {
    logger.info("RequestDTO are :{}", requestDTO);
    ProductDTO productDTO = createProductService.execute(requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
  }

  // UPDATE PRODUCT
  @Operation(
      summary = "Update a product",
      description =
          "Update a product for users to reflect the latest changes. Only usable by"
              + " admins")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully created",
            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized Management",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to find product with the product id.",
            content = @Content(schema = @Schema())),
      })
  @PutMapping("/admin/products/{id}")
  public ResponseEntity<ProductDTO> updateProductDTO(
      @PathVariable("id") String id, @RequestBody ProductRequestDTO requestDTO) {
    ProductDTO productDTO =
        updateProductService.execute(new UpdateProductCommand(id, requestDTO));
    return ResponseEntity.status(HttpStatus.OK).body(productDTO);
  }

  // DELETE PRODUCT
  // CHALLENGE - > ONLY CAN DELETE IF PRODUCT BELONGS TO ADMIN
  @Operation(
          summary = "Delete a product",
          description = "Deletes a product by its ID. Only accessible by admins. Will throw if product doesn't exist or does not belong to the authenticated user.",
          security = @SecurityRequirement(name = "bearerAuth"),
          parameters = {
                  @Parameter(name = "id", description = "ID of the product to delete", required = true, example = "f2e1c75a-35e2-4a10-bbc5-8a091a987abc")
          }
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "204",
                  description = "Product deleted successfully (No Content)"
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "Unauthorized: Missing or invalid authentication token",
                  content = @Content(schema = @Schema())
          ),
          @ApiResponse(
                  responseCode = "403",
                  description = "Forbidden: Product does not belong to the current user",
                  content = @Content(schema = @Schema())
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Not Found: Product with given ID does not exist",
                  content = @Content(schema = @Schema())
          )
  })
  @DeleteMapping("/admin/products/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
    deleteProductService.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}

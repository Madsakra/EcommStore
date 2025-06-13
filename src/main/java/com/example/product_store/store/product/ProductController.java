package com.example.product_store.store.product;


import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final GetProductsService getProductsService;
    private final CreateProductService createProductService;
    private final DeleteProductService deleteProductService;
    private final SearchProductService searchProductService;
    private final UpdateProductService updateProductService;


    public ProductController(GetProductsService getProductsService,
                             CreateProductService createProductService,
                             DeleteProductService deleteProductService,
                             SearchProductService searchProductService,
                             UpdateProductService updateProductService
                             ) {
        this.getProductsService = getProductsService;
        this.createProductService = createProductService;
        this.deleteProductService = deleteProductService;
        this.searchProductService = searchProductService;
        this.updateProductService = updateProductService;
    }

    // GENERAL ENDPOINT
    // GET ALL PRODUCTS
    @GetMapping("/store/all-products")
    public ResponseEntity<List<ProductDTO>> getProducts(){
        List<ProductDTO>  products = getProductsService.execute(null);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // ONLY FOR USERS
    // SEARCH PRODUCT BY TITLE
    @GetMapping("/user/store/searchTitle/{title}")
    public ResponseEntity<List<ProductDTO>> searchProductByTitle(@PathVariable String title){
        List<ProductDTO> products = searchProductService.execute(title);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // ONLY FOR USERS
    // SEARCH PRODUCT BY DESCRIPTION
    @GetMapping("/user/store/searchDes/{description}")
    public ResponseEntity<List<ProductDTO>> searchProductByDescription(@PathVariable String description){
        List<ProductDTO> products = searchProductService.searchProductByDescription(description);
        return ResponseEntity.ok(products);
    }


    // ONLY FOR ADMINS
    // CREATE NEW PRODUCT
    @PostMapping("/admin/createProduct")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product){
        ProductDTO productDTO = createProductService.execute(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    // UPDATE PRODUCT
    // CHALLENGE - > ONLY CAN UPDATE IF PRODUCT BELONGS TO ADMIN
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ProductDTO> updateProductDTO(@PathVariable String id, @RequestBody Product product){
        ProductDTO productDTO = updateProductService.execute(new UpdateProductCommand(id,product));
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);
    }


    // DELETE PRODUCT
    // CHALLENGE - > ONLY CAN DELETE IF PRODUCT BELONGS TO ADMIN
    @DeleteMapping("/admin/deleteProduct/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id){
        deleteProductService.execute(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

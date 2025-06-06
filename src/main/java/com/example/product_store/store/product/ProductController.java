package com.example.product_store.store.product;


import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductDTO;
import com.example.product_store.store.product.service.CreateProductService;
import com.example.product_store.store.product.service.DeleteProductService;
import com.example.product_store.store.product.service.GetProductsService;
import com.example.product_store.store.product.service.SearchProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
public class ProductController {

    private final GetProductsService getProductsService;
    private final CreateProductService createProductService;
    private final DeleteProductService deleteProductService;
    private final SearchProductService searchProductService;



    public ProductController(GetProductsService getProductsService, CreateProductService createProductService, DeleteProductService deleteProductService, SearchProductService searchProductService) {
        this.getProductsService = getProductsService;
        this.createProductService = createProductService;
        this.deleteProductService = deleteProductService;
        this.searchProductService = searchProductService;
    }

    // GET ALL PRODUCTS
    @GetMapping("/all-products")

    public ResponseEntity<List<ProductDTO>> getProducts(){
        List<ProductDTO>  products = getProductsService.execute(null);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }


    // CREATE NEW PRODUCT
    @PostMapping("/create")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product){
        ProductDTO productDTO = createProductService.execute(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    // SEARCH PRODUCT BY TITLE
    @GetMapping("/searchTitle/{title}")
    public ResponseEntity<List<ProductDTO>> searchProductByTitle(@PathVariable String title){
        List<ProductDTO> products = searchProductService.execute(title);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }


    // SEARCH PRODUCT BY DESCRIPTION
    @GetMapping("/searchDes/{description}")
    public ResponseEntity<List<ProductDTO>> searchProductByDescription(@PathVariable String description){
        List<ProductDTO> products = searchProductService.searchProductByDescription(description);
        return ResponseEntity.ok(products);
    }


    // DELETE PRODUCT
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id){
        deleteProductService.execute(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

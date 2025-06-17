package com.example.product_store.store.product;



import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.service.*;
import org.springframework.data.domain.Page;
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
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "false") boolean all
    ){

        if (all){
            List<ProductDTO>  products = getProductsService.execute(null);
            return ResponseEntity.status(HttpStatus.OK).body(products);
        }

        int p = page !=null ? page :0;
        int s = size !=null ? size :10;

        Page<ProductDTO> pagedProducts = getProductsService.getPagedProducts(p,s);
        return ResponseEntity.status(HttpStatus.OK).body(pagedProducts.stream().toList());
    }



    // SEARCH PRODUCT BY TITLE
    @GetMapping("/products/title/{title}")
    public ResponseEntity<List<ProductDTO>> searchProductByTitle(@PathVariable("title") String title){
        List<ProductDTO> products = searchProductService.execute(title);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }


    // SEARCH PRODUCT BY DESCRIPTION
    @GetMapping("/products/description/{description}")
    public ResponseEntity<List<ProductDTO>> searchProductByDescription(@PathVariable("description") String description){
        List<ProductDTO> products = searchProductService.searchProductByDescription(description);
        return ResponseEntity.ok(products);
    }


    // ONLY FOR ADMINS
    // CREATE NEW PRODUCT
    @PostMapping("/admin/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product){
        ProductDTO productDTO = createProductService.execute(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    // UPDATE PRODUCT
    // CHALLENGE - > ONLY CAN UPDATE IF PRODUCT BELONGS TO ADMIN
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ProductDTO> updateProductDTO(@PathVariable("id") String id, @RequestBody Product product){
        ProductDTO productDTO = updateProductService.execute(new UpdateProductCommand(id,product));
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);
    }


    // DELETE PRODUCT
    // CHALLENGE - > ONLY CAN DELETE IF PRODUCT BELONGS TO ADMIN
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id){
        deleteProductService.execute(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

package com.example.store;

import com.example.product_store.store.category.CategoryRepository;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.ProductValidator;
import com.example.product_store.store.product.exceptions.ProductNotValidException;
import com.example.product_store.store.product.model.Product;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


public class ProductValidatorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock private CategoryRepository categoryRepository;


    private final ProductValidator validator = new ProductValidator(productRepository,categoryRepository);

    @Test
    void testProductValidator_whenTitleIsNull_ShouldThrowProductNotValidException(){
        Product product = new Product();
        product.setTitle(null); // Explicit null
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product Title should not be empty or null!",ex.getMessage());
    }

    @Test
    void testProductValidator_whenTitleIsEmpty_ShouldThrowProductNotValidException() {
        Product product = new Product();
        product.setTitle(""); // empty string
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product Title should not be empty or null!",ex.getMessage());
    }


    @Test
    void testProductValidator_whenStockIsNull_ShouldThrowProductNotValidException(){
        Product product = new Product();
        product.setTitle("product1");
        product.setStock(null); // Null quantity
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product stock should not be null or negative",ex.getMessage());
    }

    @Test
    void testProductValidator_whenStockIsNegative_ShouldThrowProductNotValidException(){
        Product product = new Product();
        product.setTitle("product1");
        product.setStock(-12); // Null quantity
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product stock should not be null or negative",ex.getMessage());
    }

    @Test
    void testProductValidator_whenPriceIsNull_ShouldThrowProductNotValidException(){
        Product product = new Product();
        product.setTitle("product1");
        product.setStock(100);
        product.setPrice(null);; // Null quantity
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product price should not be 0, null or negative",ex.getMessage());
    }

    @Test
    void testProductValidator_whenPriceIsZero_ShouldThrowProductNotValidException(){
        Product product = new Product();
        product.setTitle("product1");
        product.setStock(100);
        product.setPrice(BigDecimal.valueOf(0));; // Null quantity
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product price should not be 0, null or negative",ex.getMessage());
    }

    @Test
    void testProductValidator_whenPriceIsNegative_ShouldThrowProductNotValidException(){
        Product product = new Product();
        product.setTitle("product1");
        product.setStock(100);
        product.setPrice(BigDecimal.valueOf(-100));; // Null quantity
        ProductNotValidException ex = assertThrows(ProductNotValidException.class,
                () -> validator.execute(product, false));
        assertEquals("Product price should not be 0, null or negative",ex.getMessage());
    }

    @Test
    void testProductValidator_duplicateProduct_ShouldThrowProductNotValidException(){

    }

    @Test
    void testProductValidator_EmptyCategory_ShouldThrowProductNotValidException(){

    }

    @Test
    void testProductValidator_InvalidCategoryId_ShouldThrowProductNotValidException(){

    }







}

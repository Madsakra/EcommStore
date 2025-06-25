package com.example.store;

import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.service.GetProductSpecificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductSpecificationTest {

    @InjectMocks
    private GetProductSpecificationService getProductSpecificationService;

    @Test
    void testExecute_withNegativeMinPrice_shouldThrowInvalidPageRequestException(){
        ProductFilter productFilter = new ProductFilter();
        productFilter.setMinPrice(BigDecimal.valueOf(-100));

        InvalidPageRequestException ex = assertThrows(
                InvalidPageRequestException.class,
                ()->getProductSpecificationService.execute(productFilter)
        );

        assertEquals("Minimum price of product cannot be negative.",ex.getMessage());
    }

    @Test
    void testExecute_withNegativeMaxprice_shouldThrowInvalidPageRequestException(){
        ProductFilter productFilter = new ProductFilter();
        productFilter.setMinPrice(BigDecimal.valueOf(100));
        productFilter.setMaxPrice(BigDecimal.valueOf(-100));

        InvalidPageRequestException ex = assertThrows(
                InvalidPageRequestException.class,
                ()->getProductSpecificationService.execute(productFilter)
        );

        assertEquals("Maximum price of product cannot be negative.",ex.getMessage());
    }

    @Test
    void testExecute_minPriceMoreThanMaxPrice_shouldThrowInvalidPageRequestException(){
        ProductFilter productFilter = new ProductFilter();
        productFilter.setMinPrice(BigDecimal.valueOf(1000));
        productFilter.setMaxPrice(BigDecimal.valueOf(100));

        InvalidPageRequestException ex = assertThrows(
                InvalidPageRequestException.class,
                ()->getProductSpecificationService.execute(productFilter)
        );

        assertEquals("Minimum price cannot be greater than maximum price of product.",ex.getMessage());
    }

    @Test
    void testExecute_withValidPriceAndCategories_shouldReturnSpec(){
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(BigDecimal.valueOf(100));
        filter.setMaxPrice(BigDecimal.valueOf(500));
        filter.setCategoryIds(List.of("cat1", "cat2"));

        GetProductSpecificationService service = new GetProductSpecificationService();

        Specification<Product> spec = service.execute(filter);
        // NO ERRORS THROWN SHOULD BE SAFE
        assertNotNull(spec); // you can stop here or test behavior further
    }


}

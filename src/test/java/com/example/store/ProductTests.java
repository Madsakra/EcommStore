package com.example.store;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.service.GetProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class ProductTests {

    @Mock // Mock the response of something -> need the dependency to run the test
    private ProductRepository productRepository;


    @InjectMocks
    private GetProductsService getProductsService;

    @BeforeEach // things we need before starting the test
    public void setup(){
        // initialize the repository & the service
        MockitoAnnotations.openMocks(this);
    }

    // TEST WHEN FILTER IS VALID AND RETURNS PRODUCTS
    @Test
    void testProductsExist_withValidFilter_shouldReturnProductDTOS(){
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(BigDecimal.valueOf(10));
        filter.setMaxPrice(BigDecimal.valueOf(100));
        filter.setCategoryIds(List.of("cat1","cat2"));

        Pageable pageable = PageRequest.of(0,10);

        Product product1 = new Product();
        product1.setId("1");
        product1.setTitle("Product 1");

        Product product2 = new Product();
        product2.setId("2");
        product2.setTitle("Product 2");

        List<Product> mockProductsList = List.of(product1,product2);
        Page<Product> mockPage = new PageImpl<>(mockProductsList);

        // WHEN
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // ACT
        List<ProductDTO> result = getProductsService.execute(filter,pageable);

        // ASSERT
        assertEquals(2,result.size());
        assertEquals("Product 1",result.get(0).getTitle());
        verify(productRepository,times(1)).findAll(any(Specification.class),eq(pageable));
    }

    // TEST WHEN FILTER IS INVALID, MIN PRICE IS NEGATIVE
    // THROW EXCEPTION
    @Test
    void testExecute_withNegativeMinPrice_shouldThrowException(){
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(BigDecimal.valueOf(-1));

        Pageable pageable = PageRequest.of(0,10);

        // WHEN AND ASSERT THROWS
        InvalidPageRequestException exception = assertThrows(
                InvalidPageRequestException.class,
                ()-> getProductsService.execute(filter,pageable)
        );

        // ASSERT EQUALS
        assertEquals("Min price cannot be negative.",exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

    // TEST WHEN FILTER IS INVALID, MAX PRICE IS NEGATIVE
    // THROW EXCEPTION
    @Test
    void testExecute_withNegativeMaxPrice_shouldThrowException(){
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setMaxPrice(BigDecimal.valueOf(-1));

        Pageable pageable = PageRequest.of(0,10);

        // WHEN AND ASSERT THROWS
        InvalidPageRequestException exception = assertThrows(
                InvalidPageRequestException.class,
                ()-> getProductsService.execute(filter,pageable)
        );

        // ASSERT EQUALS
        assertEquals("Max price cannot be negative.",exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

    // TEST WHEN FILTER IS INVALID, MIN PRICE > MAX PRICE
    // THROW EXCEPTION
    @Test
    void testExecute_withMinPriceGreaterThanMaxPrice_shouldThrowException(){
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(BigDecimal.valueOf(100));
        filter.setMaxPrice(BigDecimal.valueOf(1));

        Pageable pageable = PageRequest.of(0,10);
        InvalidPageRequestException exception = assertThrows(
                InvalidPageRequestException.class,
                ()->getProductsService.execute(filter,pageable)
        );
        assertEquals("Min price cannot be greater than max price.",exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

}

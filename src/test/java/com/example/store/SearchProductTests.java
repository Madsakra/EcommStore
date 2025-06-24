package com.example.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.service.GetProductSpecificationService;
import com.example.product_store.store.product.service.SearchProductService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

public class SearchProductTests {

  @Mock private ProductRepository productRepository;
  @Mock private GetProductSpecificationService getProductSpecificationService;

  @InjectMocks private SearchProductService searchProductService;

  private Product product1;
  private Product product2;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // CATEGORIES STORED IN PRODUCTS FOR TESTING
    List<Category> categories1 =
        new ArrayList<>(List.of(new Category("cat_1", "category1")));
    List<Category> categories2 =
        new ArrayList<>(List.of(new Category("cat_2", "category2")));

    // PRODUCTS USED OF TESTING
    this.product1 =
        new Product(
            "1",
            "Macbook",
            "Computer",
            10,
            BigDecimal.valueOf(1000),
            "admin1",
            categories1);
    this.product2 =
        new Product(
            "2",
            "Macdonalds",
            "Book",
            10,
            BigDecimal.valueOf(500),
            "admin2",
            categories2);
  }

  // TEST WHEN FILTER IS VALID AND RETURNS PRODUCT WITH PARTICULAR TITLE
  // PRODUCT WITH DIFFERENT DESCRIPTION
  // SEARCH BY COMMON PREFIX - Mac
  // SEARCH BY DESCRIPTION - Computer
  @Test
  void testExecute_withValidSearchFilters_shouldReturnProductDTOS() {
    // GIVEN
    ProductFilter filter =
        new ProductFilter(
            "Mac",
            "Computer",
            List.of("cat_1"),
            BigDecimal.valueOf(450),
            BigDecimal.valueOf(1000));
    Pageable pageable = PageRequest.of(0, 10);

    // PUT PRODUCT 1 & 2 INTO LIST TO MOCK RESULT AND FILTER OUT SPECIFICATION
    List<Product> mockProductsList = List.of(product1, product2);
    List<Product> filtered =
        mockProductsList.stream()
            .filter(
                p ->
                    p.getTitle().startsWith("Mac")
                        && "Computer".equals(p.getDescription()))
            .toList();
    Page<Product> mockPage = new PageImpl<>(filtered);

    // WHEN
    Specification<Product> mockSpec = (root, query, cb) -> cb.conjunction();
    when(getProductSpecificationService.execute(filter)).thenReturn(mockSpec);
    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(mockPage);

    // ACT
    List<ProductDTO> result = searchProductService.execute(filter, pageable);

    // ASSERT
    assertEquals(1, result.size());
    assertEquals("Macbook", result.get(0).getTitle());
    assertEquals("Computer", result.get(0).getDescription());
    verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  // TEST WITH VALID FILTER BUT RETURN NOTHING WHEN SEARCHING FOR PRODUCT
  // LEGITIMATE SEARCH FILTERS THAT WON'T THROW ERRORS
  @Test
  void testExecute_withValidSearchFilters_shouldReturnEmpty() {
    // GIVEN
    ProductFilter filter =
        new ProductFilter(
            "Cheese Burger",
            "Food",
            List.of("cat_1"),
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(500));
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> mockPage = new PageImpl<>(new ArrayList<>());

    // WHEN
    Specification<Product> mockSpec = (root, query, cb) -> cb.conjunction();
    when(getProductSpecificationService.execute(filter)).thenReturn(mockSpec);
    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(mockPage);

    // ACT
    List<ProductDTO> result = searchProductService.execute(filter, pageable);

    // ASSERT
    assertEquals(0, result.size());
    verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

    // TEST WHEN TITLE IS NULL
    // THROW EXCEPTION
    @Test
    void testExecute_withNullTitle_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setTitle(null);


        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        InvalidPageRequestException exception =
                assertThrows(
                        InvalidPageRequestException.class,
                        () -> searchProductService.execute(filter, pageable));

        // ASSERT EQUALS
        assertEquals("Title cannot be null or empty!", exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

    // TEST WHEN TITLE IS BLANK
    // THROW EXCEPTION
    @Test
    void testExecute_withBlankTitle_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setTitle("");


        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        InvalidPageRequestException exception =
                assertThrows(
                        InvalidPageRequestException.class,
                        () -> searchProductService.execute(filter, pageable));

        // ASSERT EQUALS
        assertEquals("Title cannot be null or empty!", exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }






    // TEST WHEN FILTER IS INVALID, MIN PRICE IS NEGATIVE
    // THROW EXCEPTION
    @Test
    void testExecute_withNegativeMinPrice_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setTitle("Mac");
        filter.setMinPrice(BigDecimal.valueOf(-1));

        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        when(getProductSpecificationService.execute(filter))
                .thenThrow(new InvalidPageRequestException("Min price cannot be negative."));

        InvalidPageRequestException exception =
                assertThrows(
                        InvalidPageRequestException.class,
                        () -> searchProductService.execute(filter, pageable));

        // ASSERT EQUALS
        assertEquals("Min price cannot be negative.", exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

    // TEST WHEN FILTER IS INVALID, MAX PRICE IS NEGATIVE
    // THROW EXCEPTION
    @Test
    void testExecute_withNegativeMaxPrice_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setTitle("Mac");
        filter.setMaxPrice(BigDecimal.valueOf(-1));
        Pageable pageable = PageRequest.of(0, 10);

        when(getProductSpecificationService.execute(filter))
                .thenThrow(new InvalidPageRequestException("Max price cannot be negative."));

        // WHEN AND ASSERT THROWS
        InvalidPageRequestException exception =
                assertThrows(
                        InvalidPageRequestException.class,
                        () -> searchProductService.execute(filter, pageable));

        // ASSERT EQUALS
        assertEquals("Max price cannot be negative.", exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

    // TEST WHEN FILTER IS INVALID, MIN PRICE > MAX PRICE
    // THROW EXCEPTION
    @Test
    void testExecute_withMinPriceGreaterThanMaxPrice_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();
        filter.setTitle("Mac");
        filter.setMinPrice(BigDecimal.valueOf(1000));
        filter.setMaxPrice(BigDecimal.valueOf(450));
        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        when(getProductSpecificationService.execute(filter))
                .thenThrow(
                        new InvalidPageRequestException(
                                "Min price cannot be greater than max price."));

        // ASSERT
        InvalidPageRequestException exception =
                assertThrows(
                        InvalidPageRequestException.class,
                        () -> searchProductService.execute(filter, pageable));

        assertEquals("Min price cannot be greater than max price.", exception.getMessage());
        verify(productRepository, never())
                .findAll((Specification<Product>) any(), (Pageable) any());
    }

    // NEGATIVE PAGE
    @Test
    void testExecute_withNegativePageNumber_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();

        // ASSERT AND THROW
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Pageable pageable = PageRequest.of(-1, 10); // throws here
                    searchProductService.execute(filter, pageable);
                });
    }

    @Test
    void testExecute_withNegativePageSize_shouldThrowException() {
        // GIVEN
        ProductFilter filter = new ProductFilter();

        // ASSERT AND THROW
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Pageable pageable = PageRequest.of(0, -10); // throws here
                    searchProductService.execute(filter, pageable);
                });
    }





}

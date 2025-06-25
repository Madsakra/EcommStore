package com.example.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.dto.ProductDTO;
import com.example.product_store.store.product.exceptions.InvalidPageRequestException;
import com.example.product_store.store.product.model.Product;
import com.example.product_store.store.product.model.ProductFilter;
import com.example.product_store.store.product.service.GetProductSpecificationService;
import com.example.product_store.store.product.service.GetProductsService;
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

public class GetProductsTests {

  @Mock // Mock the response of something -> need the dependency to run the test
  private ProductRepository productRepository;
  @Mock private GetProductSpecificationService getProductSpecificationService;

  @InjectMocks private GetProductsService getProductsService;

  private Product product1;
  private Product product2;

  @BeforeEach // things we need before starting the test
  public void setup() {
    // initialize the repository & the service
    MockitoAnnotations.openMocks(this);
    List<Category> categories1 =
        new ArrayList<>(List.of(new Category("cat_1", "category1")));
    List<Category> categories2 =
        new ArrayList<>(List.of(new Category("cat_2", "category2")));

    this.product1 =
        new Product(
            "1",
            "Product 1",
            "Computer",
            10,
            BigDecimal.valueOf(1000),
            "admin1",
            categories1);
    this.product2 =
        new Product(
            "2", "Product 2", "Book", 10, BigDecimal.valueOf(500), "admin2", categories2);
  }

  // TEST WHEN FILTER IS VALID AND RETURNS PRODUCTS
  // TEST WILL RETURN PRODUCT 1 ONLY
  @Test
  void testProductsExist_withValidFilters_shouldReturnProductDTOs() {
    // GIVEN
    ProductFilter filter = new ProductFilter(null,null,List.of("cat_1"),BigDecimal.valueOf(450),BigDecimal.valueOf(1000));


    Pageable pageable = PageRequest.of(0, 10);

    List<Product> mockProductsList = List.of(product1, product2);
    List<Product> filtered =
        mockProductsList.stream()
            .filter(
                p ->
                    p.getTitle().startsWith("Product 1")
                        && "Computer".equals(p.getDescription()))
            .toList();
    Page<Product> mockPage = new PageImpl<>(filtered);

    // WHEN
    Specification<Product> mockSpec = (root, query, cb) -> cb.conjunction();
    when(getProductSpecificationService.execute(filter)).thenReturn(mockSpec);
    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(mockPage);

    // ACT
    List<ProductDTO> result = getProductsService.execute(filter, pageable);

    // ASSERT
    assertEquals(1, result.size());
    assertEquals("1", result.get(0).getId());
    assertEquals("Product 1", result.get(0).getTitle());
    assertEquals("Computer", result.get(0).getDescription());
    assertEquals("cat_1",result.get(0).getCategories().get(0).getId());
    assertEquals("category1",result.get(0).getCategories().get(0).getCategoryName());
    assertEquals(BigDecimal.valueOf(1000), result.get(0).getPrice());
    verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  // TEST WITHOUT FILTER, RETURNS ALL PRODUCTS SUCCESS
  @Test
  void testExecute_withoutFilters_shouldReturnProductList() {
    ProductFilter filter = new ProductFilter(); // empty filter
    Pageable pageable = PageRequest.of(0, 10);
    List<Product> mockProductsList = List.of(product1, product2);
    Page<Product> mockPage = new PageImpl<>(mockProductsList);

    // GIVEN
    Specification<Product> mockSpec = (root, query, cb) -> cb.conjunction();
    when(getProductSpecificationService.execute(any())).thenReturn(mockSpec);
    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(mockPage);

    // ACT
    List<ProductDTO> result = getProductsService.execute(filter, pageable);

    // ASSERT
    assertEquals(2, result.size());
    assertEquals("Product 1", result.get(0).getTitle());
    verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  // TEST WHEN FILTER IS INVALID, MIN PRICE IS NEGATIVE
  // THROW EXCEPTION
  @Test
  void testExecute_withNegativeMinPrice_shouldThrowInvalidPageException() {
    // GIVEN
    ProductFilter filter = new ProductFilter();
    filter.setMinPrice(BigDecimal.valueOf(-1));

    Pageable pageable = PageRequest.of(0, 10);

    // WHEN
    when(getProductSpecificationService.execute(filter))
        .thenThrow(new InvalidPageRequestException("Min price cannot be negative."));

    InvalidPageRequestException exception =
        assertThrows(
            InvalidPageRequestException.class,
            () -> getProductsService.execute(filter, pageable));

    // ASSERT EQUALS
    assertEquals("Min price cannot be negative.", exception.getMessage());
    verify(productRepository, never())
        .findAll((Specification<Product>) any(), (Pageable) any());
  }

  // TEST WHEN FILTER IS INVALID, MAX PRICE IS NEGATIVE
  // THROW EXCEPTION
  @Test
  void testExecute_withNegativeMaxPrice_shouldThrowInvalidPageException() {
    // GIVEN
    ProductFilter filter = new ProductFilter();
    filter.setMaxPrice(BigDecimal.valueOf(-1));
    Pageable pageable = PageRequest.of(0, 10);

    when(getProductSpecificationService.execute(filter))
        .thenThrow(new InvalidPageRequestException("Max price cannot be negative."));

    // WHEN AND ASSERT THROWS
    InvalidPageRequestException exception =
        assertThrows(
            InvalidPageRequestException.class,
            () -> getProductsService.execute(filter, pageable));

    // ASSERT EQUALS
    assertEquals("Max price cannot be negative.", exception.getMessage());
    verify(productRepository, never())
        .findAll((Specification<Product>) any(), (Pageable) any());
  }

  // TEST WHEN FILTER IS INVALID, MIN PRICE > MAX PRICE
  // THROW EXCEPTION
  @Test
  void testExecute_withMinPriceGreaterThanMaxPrice__shouldThrowInvalidPageException() {
    // GIVEN
    ProductFilter filter = new ProductFilter();
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
            () -> getProductsService.execute(filter, pageable));

    assertEquals("Min price cannot be greater than max price.", exception.getMessage());
    verify(productRepository, never())
        .findAll((Specification<Product>) any(), (Pageable) any());
  }

  // NEGATIVE PAGE
  @Test
  void testExecute_withNegativePageNumber__shouldThrowIlegalArgumentException() {
    // GIVEN
    ProductFilter filter = new ProductFilter();

    // ASSERT AND THROW
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          Pageable pageable = PageRequest.of(-1, 10); // throws here
          getProductsService.execute(filter, pageable);
        });
  }

  @Test
  void testExecute_withNegativePageSize__shouldThrowIlegalArgumentException() {
    // GIVEN
    ProductFilter filter = new ProductFilter();

    // ASSERT AND THROW
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          Pageable pageable = PageRequest.of(0, -10); // throws here
          getProductsService.execute(filter, pageable);
        });
  }
}

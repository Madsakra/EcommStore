package com.example.order;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.service.ProductRetrievalService;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ProductRetrievalServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductRetrievalService productRetrievalService;


    @Test
    void testExecute_withMissingProducts_shouldThrowProductNotFoundException(){
        // GIVEN
        // USED FOR METHOD SIGNATURE
        List<OrderCreationRequest> mockedRequest = List.of(
                new OrderCreationRequest("p1",2),
                new OrderCreationRequest("p2",2)
        );

        // ONLY 1 PRODUCT EXIST IN THE DB
        Product existingProduct = new Product();
        existingProduct.setId("p1");

        when(productRepository.findAllById(List.of("p1","p2"))).thenReturn(List.of(existingProduct));

        // WHEN & THEN
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, ()-> productRetrievalService.execute(mockedRequest));

        // ASSERT TRUE
        // P2 IS NOT FOUND
        assertTrue(ex.getMessage().contains("p2"));
    }

    @Test
    void testExecute_withProductsPresent_shouldReturnProductMap(){
        // GIVEN
        // USED FOR METHOD SIGNATURE
        List<OrderCreationRequest> mockedRequest = List.of(
                new OrderCreationRequest("p1",2),
                new OrderCreationRequest("p2",2)
        );

        Product product1 = new Product();
        product1.setId("p1");

        Product product2 = new Product();
        product2.setId("p2");

        // WHEN
        when(productRepository.findAllById(List.of("p1","p2"))).thenReturn(List.of(product1,product2));

        // ACT
        Map<String, Product> result = productRetrievalService.execute(mockedRequest);

        // ASSERT
        assertEquals(2,result.size());
        assertTrue(result.containsKey("p1"));
        assertTrue(result.containsKey("p2"));
    }


}

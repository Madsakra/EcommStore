package com.example.orders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.service.OrdersValidationService;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.exceptions.ProductNotFoundException;
import com.example.product_store.store.product.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class OrderValidationServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrdersValidationService ordersValidationService;

    @Test
    void testExecute_shouldReturnProductMapIfProductsExist(){
        // GIVEN
        // USER PAYLOAD
        OrderCreationRequest req1 = new OrderCreationRequest("p1",1);
        OrderCreationRequest req2 = new OrderCreationRequest("p2",2);
        List<OrderCreationRequest> requests = List.of(req1,req2);

        // WILL RETURN WHEN SERVICE CALLS REPO
        Product product1 = new Product();
        product1.setId("p1");

        Product product2 = new Product();
        product2.setId("p2");
        List<Product> products = List.of(product1, product2);

        // WHEN
        when(productRepository.findAllByIdForUpdate(List.of("p1","p2"))).thenReturn(products);

        // ACT
        Map<String,Product> result = ordersValidationService.execute(requests);

        // ASSERT
        assertEquals(2,result.size());
        assertTrue(result.containsKey("p1"));
        assertTrue(result.containsKey("p2"));
        verify(productRepository).findAllByIdForUpdate(List.of("p1","p2"));

    }

    // WHEN PRODUCTS 1 PRODUCT STATED BY CLIENT IS MISSING
    @Test
    void shouldThrowException_whenSomeProductsAreMissing(){
        // GIVEN
        // USER PAYLOAD
        OrderCreationRequest req1 = new OrderCreationRequest("p1",1);
        OrderCreationRequest req2 = new OrderCreationRequest("p2",2);
        List<OrderCreationRequest> requests = List.of(req1,req2);

        // WILL RETURN WHEN SERVICE CALLS REPO
        Product product1 = new Product();
        product1.setId("p1");

        Product product2 = new Product();
        product2.setId("p2");
        List<Product> products = List.of(product1);

        // WHEN
        when(productRepository.findAllByIdForUpdate(List.of("p1","p2"))).thenReturn(products);

        // Assert & throw
        ProductNotFoundException ex = assertThrows(
                ProductNotFoundException.class,
                ()->ordersValidationService.execute(requests)
        );

        assertTrue(ex.getMessage().contains("p2"));
        verify(productRepository).findAllByIdForUpdate(List.of("p1","p2"));

    }

}

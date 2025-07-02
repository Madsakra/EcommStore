package com.example.orders.actions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.events.StartInventoryEvent;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.service.actions.InventoryReductionService;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InventoryReductionServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private InventoryReductionService inventoryReductionService;

  @Test
  void testExecute_shouldReturnTrue() {

    // GIVEN
      // CLIENT PAYLOAD
    OrderCreationRequest request1 = new OrderCreationRequest();
    request1.setId("prod-1");
    request1.setQuantity(2);

    OrderCreationRequest request2 = new OrderCreationRequest();
    request2.setId("prod-2");
    request2.setQuantity(3);

    StartInventoryEvent startInventoryEvent = new StartInventoryEvent();
    startInventoryEvent.setRequests(List.of(request1, request2));
    // ------------------------------

      // id's to be searched by repository
      List<String> mockedIds = List.of("prod-1", "prod-2");

    // Products returned by repo
    Product product1 = new Product();
    product1.setId("prod-1");
    product1.setStock(10);

    Product product2 = new Product();
    product2.setId("prod-2");
    product2.setStock(5);
    List<Product> mockedProducts = List.of(product1, product2);

    // WHEN
    when(productRepository.findAllById(mockedIds)).thenReturn(mockedProducts);

    // ACT
    boolean result = inventoryReductionService.execute(startInventoryEvent);

    // ASSERT
    assertTrue(result);
    assertEquals(8, product1.getStock());
    assertEquals(2, product2.getStock());

    verify(productRepository).findAllById(mockedIds);
    verify(productRepository).saveAll(mockedProducts);
  }

  @Test
    void testExecute_productNotFound_shouldThrowProductStockException(){
      // GIVEN
      // CLIENT PAYLOAD
      OrderCreationRequest request1 = new OrderCreationRequest();
      request1.setId("prod-1");
      request1.setQuantity(2);

      OrderCreationRequest request2 = new OrderCreationRequest();
      request2.setId("prod-2");
      request2.setQuantity(3);

      StartInventoryEvent startInventoryEvent = new StartInventoryEvent();
      startInventoryEvent.setRequests(List.of(request1, request2));
      // ------------------------------

      // id's to be searched by repository
      List<String> mockedIds = List.of("prod-1", "prod-2");

      // Products returned by repo
      Product product1 = new Product();
      product1.setId("prod-1");
      product1.setStock(10);

      Product product2 = new Product();
      product2.setId("prod-2");
      product2.setStock(5);
      List<Product> mockedProducts = List.of(product1); // ---> only prod-1 is returned

      // WHEN
      when(productRepository.findAllById(mockedIds)).thenReturn(mockedProducts);


      // ASSERT & THROW
      ProductStockException exception = assertThrows(
              ProductStockException.class,
              ()->inventoryReductionService.execute(startInventoryEvent)
      );

      assertTrue(exception.getMessage().contains("Product not found: prod-2"));
  }

  @Test
    void testExecute_productInsufficient_shouldThrowProductStockException(){
      // GIVEN
      // CLIENT PAYLOAD
      OrderCreationRequest request1 = new OrderCreationRequest();
      request1.setId("prod-1");
      request1.setQuantity(5);

      OrderCreationRequest request2 = new OrderCreationRequest();
      request2.setId("prod-2");
      request2.setQuantity(15);

      StartInventoryEvent startInventoryEvent = new StartInventoryEvent();
      startInventoryEvent.setRequests(List.of(request1, request2));
      // ------------------------------

      // id's to be searched by repository
      List<String> mockedIds = List.of("prod-1", "prod-2");

      // Products returned by repo
      Product product1 = new Product();
      product1.setId("prod-1");
      product1.setStock(10);

      Product product2 = new Product();
      product2.setId("prod-2");
      product2.setStock(5);
      List<Product> mockedProducts = List.of(product1,product2); // ---> only prod-1 is returned


      // WHEN
      when(productRepository.findAllById(mockedIds)).thenReturn(mockedProducts);

      // ASSERT AND THROW
      ProductStockException exception = assertThrows(
              ProductStockException.class,
              ()->inventoryReductionService.execute(startInventoryEvent)
      );

      assertTrue(exception.getMessage().contains("Insufficient stock for: prod-2"));
  }


}

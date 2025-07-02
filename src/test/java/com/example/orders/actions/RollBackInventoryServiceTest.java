package com.example.orders.actions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.events.InventoryCompletedEvent;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.service.actions.RollBackInventoryService;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RollBackInventoryServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private RollBackInventoryService rollBackInventoryService;

  @Test
  void testExecute_shouldReturnTrue_IfSuccess() {
    // GIVEN
    InventoryCompletedEvent event = new InventoryCompletedEvent();
    OrderCreationRequest req1 = new OrderCreationRequest();
    req1.setId("prod-1");
    req1.setQuantity(2);

    OrderCreationRequest req2 = new OrderCreationRequest();
    req2.setId("prod-2");
    req2.setQuantity(2);

    event.setRequests(
        List.of(req1, req2)); // ->  REFUND WILL ADD ON TO THE PRODUCT INSTEAD

    // MOCKED PRODUCTS
    Product mockedProd1 = new Product();
    mockedProd1.setId("prod-1");
    mockedProd1.setStock(2);

    Product mockedProd2 = new Product();
    mockedProd2.setId("prod-2");
    mockedProd2.setStock(2);

    List<String> ids = List.of("prod-1", "prod-2");
    List<Product> mockedProducts = List.of(mockedProd1, mockedProd2);
    when(productRepository.findAllById(ids)).thenReturn(mockedProducts);

    // ACT
    boolean result = rollBackInventoryService.execute(event);

    // ASSERT
    assertTrue(result);
    assertEquals(4, mockedProd1.getStock()); // 2+2
    assertEquals(4, mockedProd2.getStock()); // 2+2

    verify(productRepository).findAllById(ids);
    verify(productRepository).saveAll(mockedProducts);
  }

  @Test
    void testExecute_shouldThrowException_whenProductNotFound(){
      // GIVEN
      InventoryCompletedEvent event = new InventoryCompletedEvent();
      OrderCreationRequest req1 = new OrderCreationRequest();
      req1.setId("prod-1");
      req1.setQuantity(2);

      OrderCreationRequest req2 = new OrderCreationRequest();
      req2.setId("prod-2");
      req2.setQuantity(2);

      event.setRequests(
              List.of(req1, req2)); // ->  REFUND WILL ADD ON TO THE PRODUCT INSTEAD

      // MOCKED PRODUCTS
      Product mockedProd1 = new Product();
      mockedProd1.setId("prod-1");
      mockedProd1.setStock(2);

      Product mockedProd2 = new Product();
      mockedProd2.setId("prod-2");
      mockedProd2.setStock(2);

      List<String> ids = List.of("prod-1", "prod-2");
      when(productRepository.findAllById(ids)).thenReturn(List.of(mockedProd1));

      // ASSERT
      ProductStockException exception = assertThrows(
              ProductStockException.class,
              ()->rollBackInventoryService.execute(event)
      );

      assertTrue(exception.getMessage().contains("Product not found: prod-2"));
  }
}

package com.example.order.recovery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.exceptions.ProductStockException;
import com.example.product_store.order.service.recovery.RestockService;
import com.example.product_store.store.product.ProductRepository;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestockServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private RestockService restockService;

  @Test
  void testExecute_nullMessage_shouldThrowException() {
    EmptyKafkaMessageException ex = assertThrows(EmptyKafkaMessageException.class, () -> restockService.execute(null));
    assertEquals("The current kafka message is empty", ex.getMessage());
  }

  @Test
  void testExecute_emptyMessage_shouldThrowException() {
    String kafkaMessage = "";
    EmptyKafkaMessageException ex =
        assertThrows(EmptyKafkaMessageException.class, () -> restockService.execute(kafkaMessage));
    assertEquals("The current kafka message is empty", ex.getMessage());
  }

  @Test
  void testExecute_restockSuccessful() throws JsonProcessingException {
    // GIVEN
    String kafkaMessage =
        """
        {
          "eventType": "PaymentDenied",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00,
            "orderCreationRequests": [
              { "id": "product1", "quantity": 5 },
              { "id": "product2", "quantity": 5 }
            ]
          }
        }
        """;

    String productId1 = "product1";
    String productId2 = "product2";

    Product product1 = new Product();
    product1.setId(productId1);
    product1.setStock(10);

    Product product2 = new Product();
    product2.setId(productId2);
    product2.setStock(5);

    // Mock repository: return products when queried by ID
    when(productRepository.findAllById(List.of(productId1, productId2))).thenReturn(List.of(product1, product2));
    when(productRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    // WHEN
    restockService.execute(kafkaMessage);

    // THEN: Check if stock was correctly deducted
    assertEquals(15, product1.getStock()); // 10 + 5
    assertEquals(10, product2.getStock()); // 5 + 5

    // THEN: Verify products were saved
    ArgumentCaptor<List<Product>> productCaptor = ArgumentCaptor.forClass(List.class);
    verify(productRepository).saveAll(productCaptor.capture());

    List<Product> savedProducts = productCaptor.getValue();
    assertEquals(2, savedProducts.size());
  }

  @Test
  void testExecute_productNotFound_shouldThrowException() throws JsonProcessingException {
    // GIVEN
    String kafkaMessage =
        """
        {
          "eventType": "PaymentDenied",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00,
            "orderCreationRequests": [
              { "id": "product1", "quantity": 5 },
              { "id": "product2", "quantity": 5 }
            ]
          }
        }
        """;

    String productId1 = "product1";
    String productId2 = "product2";

    Product product2 = new Product();
    product2.setId(productId2);
    product2.setStock(5);

    // WHEN
    when(productRepository.findAllById(List.of(productId1, productId2))).thenReturn(List.of(product2));
    ProductStockException ex = assertThrows(ProductStockException.class, () -> restockService.execute(kafkaMessage));

    assertTrue(ex.getMessage().contains(productId1));
  }

  @Test
  void testExecute_malformedJson_shouldThrowException(){
    // GIVEN
    String kafkaMessage =
            """
         
              "eventType": "PaymentDenied",
              "payload": {
                "customerId": "user123",
                "orderId": "order123",
                "totalPrice": 50.00,
                "orderCreationRequests": [
                  { "id": "product1", "quantity": 5 },
                  { "id": "product2", "quantity": 5 }
                ]
              }
            }
            """;

    assertThrows(JsonProcessingException.class,()->restockService.execute(kafkaMessage));
  }


}

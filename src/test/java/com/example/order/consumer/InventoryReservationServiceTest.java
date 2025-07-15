package com.example.order.consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.service.consumer.InventoryReservationService;
import com.example.product_store.store.product.repositories.ProductRepository;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InventoryReservationServiceTest {

  @Mock private ProductRepository productRepository;

  @Mock private ObjectMapper objectMapper;

  @Mock private OutboxRepository outboxRepository;

  @InjectMocks private InventoryReservationService inventoryReservationService;

  @Test
  void testExecute_withEmptyKafkaMessage_shouldThrowException() {
    String message = "";
    EmptyKafkaMessageException exception =
        assertThrows(EmptyKafkaMessageException.class, () -> inventoryReservationService.execute(message));
  }

  @Test
  void testExecute_withNullKafkaMessage_shouldThrowException() {
    EmptyKafkaMessageException exception =
        assertThrows(EmptyKafkaMessageException.class, () -> inventoryReservationService.execute(null));
  }

  @Test
  void testExecute_success_inventoryReserved() throws JsonProcessingException {
    // GIVEN
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

    when(outboxRepository.save(any(Outbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // VALID KAFKA MESSAGE
    String kafkaMessage =
        """
        {
          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00,
            "orderCreationRequests": [
              { "id": "product1", "quantity": 4 },
              { "id": "product2", "quantity": 2 }
            ]
          }
        }
        """;

    // WHEN
    inventoryReservationService.execute(kafkaMessage);

    // THEN: Check if stock was correctly deducted
    assertEquals(6, product1.getStock()); // 10 - 4
    assertEquals(3, product2.getStock()); // 5 - 2

    // THEN: Verify products were saved
    ArgumentCaptor<List<Product>> productCaptor = ArgumentCaptor.forClass(List.class);
    verify(productRepository).saveAll(productCaptor.capture());

    List<Product> savedProducts = productCaptor.getValue();
    assertEquals(2, savedProducts.size());

    // THEN: Verify the correct outbox event was saved
    ArgumentCaptor<Outbox> outboxCaptor = ArgumentCaptor.forClass(Outbox.class);
    verify(outboxRepository).save(outboxCaptor.capture());

    Outbox savedOutbox = outboxCaptor.getValue();
    assertEquals("InventoryReserved", savedOutbox.getType());
    assertEquals("order123", savedOutbox.getAggregateId());
    assertEquals("order-completed", savedOutbox.getAggregateType());
  }

  @Test
  void testExecute_whenProductIsNull_shouldThrowException() throws JsonProcessingException {
    // GIVEN
    String productId1 = "product1";
    String productId2 = "product2";

    Product product1 = new Product();
    product1.setId(productId1);
    product1.setStock(10);

    Product product2 = new Product();
    product2.setId(productId2);
    product2.setStock(5);

    // Mock repository: return products when queried by ID
    when(productRepository.findAllById(List.of(productId1, productId2))).thenReturn(List.of(product2));

    when(outboxRepository.save(any(Outbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // VALID KAFKA MESSAGE
    String kafkaMessage =
        """
        {
          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00,
            "orderCreationRequests": [
              { "id": "product1", "quantity": 4 },
              { "id": "product2", "quantity": 2 }
            ]
          }
        }
        """;

    inventoryReservationService.execute(kafkaMessage);
    ArgumentCaptor<Outbox> outboxCaptor = ArgumentCaptor.forClass(Outbox.class);
    verify(outboxRepository).save(outboxCaptor.capture());

    Outbox savedOutbox = outboxCaptor.getValue();
    assertEquals("InventoryFailed", savedOutbox.getType());
    assertEquals("order123", savedOutbox.getAggregateId());
    assertEquals("order-completed", savedOutbox.getAggregateType());
  }

  @Test
  void testExecute_whenStockIsLessThanQuantity_shouldThrowException() throws JsonProcessingException {
    // GIVEN
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

    when(outboxRepository.save(any(Outbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // VALID KAFKA MESSAGE
    String kafkaMessage =
        """
        {
          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00,
            "orderCreationRequests": [
              { "id": "product1", "quantity": 20 },
              { "id": "product2", "quantity": 20 }
            ]
          }
        }
        """;

    inventoryReservationService.execute(kafkaMessage);
    ArgumentCaptor<Outbox> outboxCaptor = ArgumentCaptor.forClass(Outbox.class);
    verify(outboxRepository).save(outboxCaptor.capture());

    Outbox savedOutbox = outboxCaptor.getValue();
    assertEquals("InventoryFailed", savedOutbox.getType());
    assertEquals("order123", savedOutbox.getAggregateId());
    assertEquals("order-completed", savedOutbox.getAggregateType());
  }

  @Test
  void testExecute_malformedJson_shouldThrowJsonProcessingException() {
    String kafkaMessage =
        """

          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00,
              { "id": "product1", "quantity": 20 },
              { "id": "product2", "quantity": 20 }
                      }
        }
        """;

    assertThrows(JsonProcessingException.class, () -> inventoryReservationService.execute(kafkaMessage));
  }
}

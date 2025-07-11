package com.example.order.consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.service.consumer.OrderCompletionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderCompletionServiceTest {
  @Mock private OrderRepository orderRepository;

  @Mock private ObjectMapper objectMapper;
  @Mock private OutboxRepository outboxRepository;

  @InjectMocks private OrderCompletionService orderCompletionService;

  @Test
  void testExecute_withEmptyKafkaMessage_shouldThrowException() {
    String message = "";
    EmptyKafkaMessageException exception =
        assertThrows(EmptyKafkaMessageException.class, () -> orderCompletionService.execute(message));
  }

  @Test
  void testExecute_withNullKafkaMessage_shouldThrowException() {
    EmptyKafkaMessageException exception =
        assertThrows(EmptyKafkaMessageException.class, () -> orderCompletionService.execute(null));
  }

  @Test
  void testOrderCompletion_whenBothEventSucceeds() throws InterruptedException {
    Order mockOrder = new Order();
    mockOrder.setId("order123"); // Important! Set the ID explicitly.
    mockOrder.setCustomerId("user123");

    // Mock findById BEFORE calling execute()
    when(orderRepository.findById("order123")).thenReturn(Optional.of(mockOrder));

    String paymentMsg =
        """
        {
          "eventType": "PaymentAccepted",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
          }
        }
        """;

    String inventoryMsg =
        """
        {
          "eventType": "InventoryReserved",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
          }
        }
        """;

    Runnable paymentEvent =
        () -> {
          try {
            orderCompletionService.execute(paymentMsg);
          } catch (Exception e) {
            fail("Exception in paymentEvent: " + e.getMessage());
          }
        };

    Runnable inventoryEvent =
        () -> {
          try {
            orderCompletionService.execute(inventoryMsg);
          } catch (Exception e) {
            fail("Exception in inventoryEvent: " + e.getMessage());
          }
        };

    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(paymentEvent);
    executor.submit(inventoryEvent);

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    // Now verify save was called
    verify(orderRepository)
        .save(argThat(order -> "Success".equals(order.getOrderStatus()) && mockOrder.getId().equals(order.getId())));
    verify(orderRepository, atLeast(1)).save(argThat(order -> "Success".equals(order.getOrderStatus())));
  }

  @Test
  void testConcurrentEvent_WhenPaymentDenied_shouldSendToOutbox() throws InterruptedException {
    Order mockOrder = new Order();
    mockOrder.setId("order123"); // Important! Set the ID explicitly.
    mockOrder.setCustomerId("user123");

    // Mock findById BEFORE calling execute()
    when(orderRepository.findById("order123")).thenReturn(Optional.of(mockOrder));

    String paymentMsg =
        """
        {
          "eventType": "PaymentDenied",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
          }
        }
        """;

    String inventoryMsg =
        """
        {
          "eventType": "InventoryReserved",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
          }
        }
        """;

    Runnable paymentEvent =
        () -> {
          try {
            orderCompletionService.execute(paymentMsg);
          } catch (Exception e) {
            fail("Exception in paymentEvent: " + e.getMessage());
          }
        };

    Runnable inventoryEvent =
        () -> {
          try {
            orderCompletionService.execute(inventoryMsg);
          } catch (Exception e) {
            fail("Exception in inventoryEvent: " + e.getMessage());
          }
        };
    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(paymentEvent);
    executor.submit(inventoryEvent);

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    verify(outboxRepository, atLeastOnce())
        .save(
            argThat(
                outbox ->
                    outbox.getAggregateType().equals("inventory-restock") && outbox.getType().equals("PaymentDenied")));
  }

  @Test
  void testConcurrentEvent_WhenInventoryReservationFailed_shouldSendToOutbox() throws InterruptedException {
    Order mockOrder = new Order();
    mockOrder.setId("order123"); // Important! Set the ID explicitly.
    mockOrder.setCustomerId("user123");

    // Mock findById BEFORE calling execute()
    when(orderRepository.findById("order123")).thenReturn(Optional.of(mockOrder));

    String paymentMsg =
        """
        {
          "eventType": "PaymentAccepted",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
          }
        }
        """;

    String inventoryMsg =
        """
        {
          "eventType": "InventoryFailed",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
          }
        }
        """;

    Runnable paymentEvent =
        () -> {
          try {
            orderCompletionService.execute(paymentMsg);
          } catch (Exception e) {
            fail("Exception in paymentEvent: " + e.getMessage());
          }
        };

    Runnable inventoryEvent =
        () -> {
          try {
            orderCompletionService.execute(inventoryMsg);
          } catch (Exception e) {
            fail("Exception in inventoryEvent: " + e.getMessage());
          }
        };
    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(paymentEvent);
    executor.submit(inventoryEvent);

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    verify(outboxRepository, atLeastOnce())
        .save(
            argThat(
                outbox ->
                    outbox.getAggregateType().equals("payment-refund") && outbox.getType().equals("InventoryFailed")));
  }
}

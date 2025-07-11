package com.example.order.consumer;
import com.example.product_store.notification.model.Notification;
import com.example.product_store.notification.repositories.NotificationRepository;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.exceptions.WrongKafkaEventException;
import com.example.product_store.order.service.ProductRetrievalService;
import com.example.product_store.order.service.consumer.NotificationService;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private ProductRetrievalService productRetrievalService;
    
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testExecute_withEmptyKafkaMessage_shouldThrowException() {
        String message = "";
        EmptyKafkaMessageException exception =
                assertThrows(EmptyKafkaMessageException.class, () -> notificationService.execute(message));
    }

    @Test
    void testExecute_withNullKafkaMessage_shouldThrowException() {
        EmptyKafkaMessageException exception =
                assertThrows(EmptyKafkaMessageException.class, () -> notificationService.execute(null));
    }
    
    
    @Test
    void testNotificationService_shouldSendOutNotificationSuccessfully() throws JsonProcessingException {
        // Arrange
        String message = """
        {
          "eventType": "NotifyAdmin",
          "payload": {
            "orderId": "order123",
            "customerId": "cust123",
                 "totalPrice": 150.00,
            "orderCreationRequests": [
              { "id": "p1", "quantity": 2 },
              { "id": "p2", "quantity": 1 }
            ]
          }
        }
        """;

        Product product1 = new Product();
        product1.setId("p1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setCreatedBy("admin1");

        Product product2 = new Product();
        product2.setId("p2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setCreatedBy("admin2");

        Map<String, Product> productMap = Map.of(
                "p1", product1,
                "p2", product2
        );

        when(productRetrievalService.execute(any())).thenReturn(productMap);

        // when
        notificationService.execute(message);

        // THEN
        verify(productRetrievalService).execute(argThat(reqList->
                reqList.size() == 2 && reqList.get(0).getId().equals("p1") && reqList.get(1).getId().equals("p2")
                ));

        verify(notificationRepository).saveAll(argThat(notificationsIterable -> {
            List<Notification> notifications = new ArrayList<>();
            notificationsIterable.forEach(notifications::add);

            if (notifications.size() != 2) return false;

            // Check notification for admin1
            boolean admin1Valid = notifications.stream().anyMatch(n ->
                    n.getAdminId().equals("admin1") &&
                            n.getClientId().equals("cust123") &&
                            n.getBatchOrderId().equals("order123") &&
                            n.getTotalPaid().compareTo(new BigDecimal("20.00")) == 0
            );

            // Check notification for admin2
            boolean admin2Valid = notifications.stream().anyMatch(n ->
                    n.getAdminId().equals("admin2") &&
                            n.getClientId().equals("cust123") &&
                            n.getBatchOrderId().equals("order123") &&
                            n.getTotalPaid().compareTo(new BigDecimal("20.00")) == 0
            );

            return admin1Valid && admin2Valid;
        }));
    }

    @Test
    void testNotificationService_WrongEventType_shouldThrowException(){
        // Arrange
        String message = """
        {
          "eventType": "Notify",
          "payload": {
            "orderId": "order123",
            "customerId": "cust123",
                 "totalPrice": 150.00,
            "orderCreationRequests": [
              { "id": "p1", "quantity": 2 },
              { "id": "p2", "quantity": 1 }
            ]
          }
        }
        """;
        WrongKafkaEventException exception = assertThrows(WrongKafkaEventException.class,()->notificationService.execute(message));
        assertEquals("Received the wrong type of event for this service", exception.getMessage());
    }

    @Test
    void testNotificationService_JsonProcessingException_shouldThrowException(){
        // Arrange
        String message = """
        
          "eventType": "Notify",
          "payload": {
            "orderId": "order123",
            "customerId": "cust123",
                 "totalPrice": 150.00,
            "orderCreationRequests": [
              { "id": "p1", "quantity": 2 },
              { "id": "p2", "quantity": 1 }
            ]
          }
        }
        """;
    assertThrows(JsonProcessingException.class,()->notificationService.execute(message));
    }

}

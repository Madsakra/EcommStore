package com.example.orders.consumers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.notification.service.CreateNotificationService;
import com.example.product_store.order.enums.OrderStatus;
import com.example.product_store.order.events.OrderCompletionEvent;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.service.consumers.OrderCompletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderCompletionServiceTest {

    @Mock
    private CreateNotificationService createNotificationService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCompletionService orderCompletionService;

    @Test
    void testOrderCompletion_shouldUpdateOrderAndNotifyOnSuccess(){
        // GIVEN
        OrderCompletionEvent event = new OrderCompletionEvent();
        event.setOrderId("order-1");
        event.setClientId("client-1");
        event.setMessage("Order processed successfully");
        event.setOrderStatus(OrderStatus.SUCCESS);
        event.setPurchasesMap(Map.of("prod-1", BigDecimal.valueOf(2))); // dummy purchases

        Order existingOrder = new Order();
        existingOrder.setId("order-1");
        // WHEN
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(existingOrder));

        // ACT
        orderCompletionService.orderCompletion(event);

        // ASSERT
        assertEquals("Success",existingOrder.getOrderStatus());
        assertEquals("Order processed successfully",existingOrder.getMessage());
        assertNotNull(existingOrder.getUpdatedAt());

        verify(createNotificationService).execute(
                eq(Map.of("prod-1", BigDecimal.valueOf(2))),
                eq("order-1"),
                eq("client-1")
        );
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void testOrderCompletion_shouldDoNothing_whenOrderNotFound(){
        // GIVEN
        // GIVEN
        OrderCompletionEvent event = new OrderCompletionEvent();
        event.setOrderId("order-999");
        event.setOrderStatus(OrderStatus.SUCCESS);

        when(orderRepository.findById("order-999")).thenReturn(Optional.empty());

        // WHEN
        orderCompletionService.orderCompletion(event);

        // THEN
        verify(orderRepository, never()).save(any());
        verify(createNotificationService, never()).execute(any(), any(), any());
    }
    @Test
    void testOrderCompletion_shouldCatchAndLogException_whenSaveFails() {
        // GIVEN
        OrderCompletionEvent event = new OrderCompletionEvent();
        event.setOrderId("order-123");
        event.setClientId("client-1");
        event.setMessage("Test Message");
        event.setOrderStatus(OrderStatus.SUCCESS);
        event.setPurchasesMap(Map.of("prod-1", BigDecimal.valueOf(2)));

        Order order = new Order();
        order.setId("order-123");

        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));
        doThrow(new RuntimeException("DB Error")).when(orderRepository).save(any());

        // WHEN
        orderCompletionService.orderCompletion(event);

        // THEN
        verify(createNotificationService).execute(any(), any(), any());
        verify(orderRepository).save(order); // still attempted
    }
}

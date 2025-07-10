package com.example.order;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.model.OrderItem;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.service.CreateOrderService;
import com.example.product_store.order.service.ProductRetrievalService;
import com.example.product_store.store.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class CreateOrderServiceTest {

    @Mock
    private ProductRetrievalService productRetrievalService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private CreateOrderService createOrderService;

    @Test
    void testCreateOrderService_success_shouldReturnOrderDTO() throws JsonProcessingException {
        String expectedJti = "user123";

        // Sample request: 2 quantities of product "p1"
        List<OrderCreationRequest> orderRequests = List.of(
                new OrderCreationRequest("p1", 2)
        );

        Product product = new Product();
        product.setId("p1");
        product.setPrice(new BigDecimal("10.00"));
        product.setCreatedBy("admin123");

        // Mock product service
        Map<String, Product> productMap = Map.of("p1", product);
        when(productRetrievalService.execute(orderRequests)).thenReturn(productMap);

        // Prepare order to be returned by repo (with ID assigned)
        Order orderToSave = new Order(expectedJti);
        orderToSave.setOrderItems(List.of(new OrderItem(product, 2, orderToSave)));
        orderToSave.setTotalPrice(new BigDecimal("20.00"));
        orderToSave.setOrderStatus("Processing");
        orderToSave.setMessage("Order received...processing now.");

        Order savedOrder = new Order(expectedJti);
        savedOrder.setId("order-123");
        savedOrder.setOrderItems(orderToSave.getOrderItems());
        savedOrder.setTotalPrice(orderToSave.getTotalPrice());
        savedOrder.setOrderStatus(orderToSave.getOrderStatus());
        savedOrder.setMessage(orderToSave.getMessage());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Prepare outbox
        Outbox savedOutbox = new Outbox("iL", "order", "order-123", "OrderCreated", "payload");
        when(outboxRepository.save(any(Outbox.class))).thenReturn(savedOutbox);

        // When
        OrderDTO result = createOrderService.execute(expectedJti, orderRequests);

        // Then
        assertNotNull(result);
        assertEquals("order-123", result.getId());
        assertEquals(new BigDecimal("20.00"), result.getTotalPrice());
        assertEquals(expectedJti, result.getCustomerId());

        verify(productRetrievalService).execute(orderRequests);
        verify(orderRepository).save(any(Order.class));
        verify(outboxRepository).save(any(Outbox.class));
    }

    @Test
    void testCreateOrderService_shouldThrowJsonProcessingException_whenSerializationFails() throws JsonProcessingException {
        String jti = "user123";
        List<OrderCreationRequest> orderRequests = List.of(new OrderCreationRequest("p1", 2));

        Product product = new Product();
        product.setId("p1");
        product.setPrice(new BigDecimal("10.00"));
        product.setCreatedBy("admin123");

        Map<String, Product> productMap = Map.of("p1", product);
        when(productRetrievalService.execute(orderRequests)).thenReturn(productMap);

        Order orderToSave = new Order(jti);
        orderToSave.setOrderItems(List.of(new OrderItem(product, 2, orderToSave)));
        orderToSave.setTotalPrice(new BigDecimal("20.00"));
        orderToSave.setOrderStatus("Processing");
        orderToSave.setMessage("Order received...processing now.");

        Order savedOrder = new Order(jti);
        savedOrder.setId("order-123");
        savedOrder.setOrderItems(orderToSave.getOrderItems());
        savedOrder.setTotalPrice(orderToSave.getTotalPrice());
        savedOrder.setOrderStatus(orderToSave.getOrderStatus());
        savedOrder.setMessage(orderToSave.getMessage());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Force ObjectMapper to throw
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        assertThrows(JsonProcessingException.class, () -> {
            createOrderService.execute(jti, orderRequests);
        });

        verify(orderRepository).save(any(Order.class));
        verify(objectMapper).writeValueAsString(any());
    }

}

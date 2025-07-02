package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.service.RetrieveAccountService;
import com.example.product_store.notification.service.CreateNotificationService;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.events.OrderCreatedEvent;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.service.CreateOrderService;
import com.example.product_store.order.service.OrdersValidationService;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class CreateOrderServiceTests {

  @Mock private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
  @Mock private RetrieveAccountService retrieveAccountService;
  @Mock private OrdersValidationService ordersValidationService;
  @Mock private OrderRepository orderRepository;
  @InjectMocks private CreateOrderService createOrderService;

  @Test
  void testExecute_shouldCreateOrderAndSendEvent() {
    // 1. GIVEN
    Account mockAccount = new Account();
    mockAccount.setId("user-123");

    Product mockProduct = new Product();
    mockProduct.setId("prod-1");
    mockProduct.setPrice(BigDecimal.valueOf(100));
    mockProduct.setCreatedBy("admin-1");

    OrderCreationRequest request = new OrderCreationRequest();
    request.setId("prod-1");
    request.setQuantity(2);

    Map<String, Product> productMap = Map.of("prod-1", mockProduct);

    Order savedOrder = new Order(mockAccount);
    savedOrder.setId("order-1");

    // 2. WHEN
    when(retrieveAccountService.execute(null)).thenReturn(mockAccount);
    when(ordersValidationService.execute(any())).thenReturn(productMap);
    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    // ACT
    OrderDTO orderDTO = createOrderService.execute(List.of(request));

    // ASSERT EQUALS
    assertEquals("order-1", orderDTO.getId());
    verify(kafkaTemplate).send(eq("order-commands"), any(OrderCreatedEvent.class));
    verify(orderRepository).save(any(Order.class));
  }
}

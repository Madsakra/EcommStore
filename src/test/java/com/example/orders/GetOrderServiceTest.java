package com.example.orders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.exceptions.OrderNotFoundException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.service.GetOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GetOrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrderService getOrderService;

    @Test
    void testExecute_shouldReturnOrderDTO_ifSuccess(){
        // GIVEN
        String orderId = "order-1";
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus("Success");
        // WHEN
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // ACT
        OrderDTO result = getOrderService.execute(orderId);

        // ASSERT
        assertNotNull(result);
        assertEquals(orderId,result.getId());
        assertEquals("Success",result.getStatus());
    }

    @Test
    void testExecute_shouldThrowOrderNotFoundException_whenOrderDoesNotExist(){
        // GIVEN
        String orderId = "order-999";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(OrderNotFoundException.class, () -> getOrderService.execute(orderId));
    }
}

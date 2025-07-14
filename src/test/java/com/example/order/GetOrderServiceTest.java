package com.example.order;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.dto.OrderDTO;
import com.example.product_store.order.exceptions.OrderNotFoundException;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.service.GetOrderService;
import com.example.product_store.store.product.exceptions.UnauthorizedManagement;
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
    void testGetOrderService_shouldReturnOrderDTO(){
        // GIVEN
        String expectedOrderId = "order123";
        String jti = "user123";


        Order expectedOrder = new Order();
        expectedOrder.setCustomerId(jti);
        expectedOrder.setId(expectedOrderId);

        // WHEN
        when(orderRepository.findById(expectedOrderId)).thenReturn(Optional.of(expectedOrder));

        // ACT
        OrderDTO orderDTO = getOrderService.execute(jti,expectedOrderId);

        // ASSERT AND VERIFY
        assertEquals(expectedOrderId,orderDTO.getId());
        assertEquals(jti,orderDTO.getCustomerId());
        verify(orderRepository).findById(expectedOrderId);
    }

    @Test
    void testGetOrderService_orderNotFound_shouldThrowOrderNotFoundException(){
        String expectedOrderId = "order123";
        String jti = "user123";

        Order expectedOrder = new Order();
        expectedOrder.setCustomerId(expectedOrderId);
        OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                ()->getOrderService.execute(jti,expectedOrderId));
        assertEquals("Order with id: "+expectedOrderId+" cannot be found.",ex.getMessage());
    }

    @Test
    void testGetOrderService_wrongUser_shouldThrowUnauthorisedException(){
        String expectedOrderId = "order123";
        String jti = "user123";

        Order expectedOrder = new Order();
        expectedOrder.setCustomerId("wrong-user");

        // WHEN
        when(orderRepository.findById(expectedOrderId)).thenReturn(Optional.of(expectedOrder));

        UnauthorizedManagement ex = assertThrows(
                UnauthorizedManagement.class,
                ()->getOrderService.execute(jti,expectedOrderId));
        assertEquals("Unauthorised Management of order.",ex.getMessage());
    }

}

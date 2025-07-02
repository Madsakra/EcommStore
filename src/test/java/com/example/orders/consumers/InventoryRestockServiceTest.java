package com.example.orders.consumers;
import static org.mockito.Mockito.*;

import com.example.product_store.order.events.InventoryCompletedEvent;
import com.example.product_store.order.service.actions.RollBackInventoryService;
import com.example.product_store.order.service.consumers.InventoryRestockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class InventoryRestockServiceTest {

    @Mock
    private RollBackInventoryService rollBackInventoryService;

    @InjectMocks
    private InventoryRestockService inventoryRestockService;

    @Captor
    private ArgumentCaptor<InventoryCompletedEvent> eventCaptor;

    @Test
    void testExecute_shouldLogSuccess_whenRestockSucceeds(){
        // GIVEN
        InventoryCompletedEvent event = new InventoryCompletedEvent();
        event.setRequests(List.of());

        when(rollBackInventoryService.execute(any())).thenReturn(true);

        // when
        inventoryRestockService.execute(event);

        // THEN
        verify(rollBackInventoryService).execute(event);
    }

    @Test
    void testExecute_shouldLogError_whenRestockThrowsException(){
        InventoryCompletedEvent event = new InventoryCompletedEvent();
        event.setRequests(List.of());

        when(rollBackInventoryService.execute(any())).thenThrow(new RuntimeException("Simulated DBB failure"));

        // WHEN
        inventoryRestockService.execute(event);

        // THEN
        verify(rollBackInventoryService).execute(event);
    }

}

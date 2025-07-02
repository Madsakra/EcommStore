package com.example.orders.consumers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.enums.InventoryStatus;
import com.example.product_store.order.events.InventoryCompletedEvent;
import com.example.product_store.order.events.StartInventoryEvent;
import com.example.product_store.order.service.consumers.StartInventoryProcessingService;
import com.example.product_store.order.service.actions.InventoryReductionService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class StartInventoryProcessingServiceTest {
  @Mock private KafkaTemplate<String, InventoryCompletedEvent> kafkaTemplate;

  @Mock private InventoryReductionService inventoryReductionService;

  @InjectMocks private StartInventoryProcessingService startInventoryProcessingService;

  @Test
  void shouldSendSuccessEvent_whenInventoryReductionSucceeds() {
    StartInventoryEvent event =
        new StartInventoryEvent(
            "order-1", Map.of(), List.of(), InventoryStatus.PROCESSING, Map.of());

    // WHEN
    when(inventoryReductionService.execute(event)).thenReturn(true);

    // ACT
    startInventoryProcessingService.execute(event);

    // ASSERT
    // USING ARGUMENT CAPTOR TO CHECK THE INPUT PLACED IN PARAMETERS
    ArgumentCaptor<InventoryCompletedEvent> captor =
        ArgumentCaptor.forClass(InventoryCompletedEvent.class);

    // ENSURE THE EVENT SENT IN IS CORRECT
    verify(kafkaTemplate).send(eq("inventory-events"), captor.capture());
    InventoryCompletedEvent sentEvent = captor.getValue();
    assertEquals(InventoryStatus.SUCCESS, sentEvent.getStatus());
    assertEquals("order-1", sentEvent.getOrderId());
    assertTrue(sentEvent.getMessage().contains("deducted stock"));
  }


  @Test
    void shouldSendFailedEvent_WhenInventoryReductionThrowsException(){
      // GIVEN
      StartInventoryEvent event =
              new StartInventoryEvent(
                      "order-2", Map.of(), List.of(), InventoryStatus.PROCESSING, Map.of());

      // WHEN
      when(inventoryReductionService.execute(event)).thenThrow(new RuntimeException("Simulated inventory Failure"));

      // ACT
      startInventoryProcessingService.execute(event);

      // ASSERT
      ArgumentCaptor<InventoryCompletedEvent> captor = ArgumentCaptor.forClass(InventoryCompletedEvent.class);
      verify(kafkaTemplate).send(eq("inventory-events"), captor.capture());

      InventoryCompletedEvent sentEvent = captor.getValue();
      assertEquals(InventoryStatus.FAILED, sentEvent.getStatus());
      assertEquals("order-2", sentEvent.getOrderId());
      assertTrue(sentEvent.getMessage().contains("Inventory error"));


  }
}

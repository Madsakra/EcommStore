package com.example.product_store.order.events;

import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.order.enums.InventoryStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCompletedEvent {
  private String orderId;
  private List<OrderCreationRequest> requests;
  private InventoryStatus status;
  private String message;

  public InventoryCompletedEvent(
      StartInventoryEvent event, InventoryStatus status, String message) {
    this.orderId = event.getOrderId();
    this.requests = event.getRequests();
    this.status = status;
    this.message = message;
  }
}

package com.example.product_store.order.events;

import com.example.product_store.order.enums.InventoryStatus;
import com.example.product_store.order.dto.OrderCreationRequest;
import com.example.product_store.store.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCompletedEvent {
    private String orderId;
    private Map<String, Product> productMap;
    private List<OrderCreationRequest> requests;
    private InventoryStatus status;
    private String message;

    public InventoryCompletedEvent(StartInventoryEvent event, InventoryStatus status, String message){
        this.orderId = event.getOrderId();
        this.requests = event.getRequests();
        this.productMap = event.getProductMap();
        this.status = status;
        this.message = message;
    }
}

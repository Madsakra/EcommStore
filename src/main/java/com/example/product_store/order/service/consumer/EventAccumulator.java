package com.example.product_store.order.service.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAccumulator {
    private String paymentStatus;
    private String inventoryStatus;

    public boolean hasPaymentEvent() {
        return paymentStatus != null;
    }

    public boolean hasInventoryEvent() {
        return inventoryStatus != null;
    }
    public boolean isReady() {
        return hasPaymentEvent() && hasInventoryEvent();
    }
}

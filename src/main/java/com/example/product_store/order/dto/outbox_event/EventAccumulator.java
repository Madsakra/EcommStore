package com.example.product_store.order.dto.outbox_event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicReference;
// Specifically used in order completion
// to receive multiple events at the same time
// may not be the right implementation
// but a replacement for microservices
@Data
public class EventAccumulator {
    private final AtomicReference<String> paymentStatus = new AtomicReference<>();
    private final AtomicReference<String> inventoryStatus = new AtomicReference<>();

    public boolean hasPaymentEvent() {
        return paymentStatus.get() != null;
    }

    public boolean hasInventoryEvent() {
        return inventoryStatus.get() != null;
    }

    public boolean isReady() {
        return hasPaymentEvent() && hasInventoryEvent();
    }

    public void setPaymentStatus(String status) {
        paymentStatus.set(status);
    }

    public void setInventoryStatus(String status) {
        inventoryStatus.set(status);
    }

    public String getPaymentStatus() {
        return paymentStatus.get();
    }

    public String getInventoryStatus() {
        return inventoryStatus.get();
    }
}

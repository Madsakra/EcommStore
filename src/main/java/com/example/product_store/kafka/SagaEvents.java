package com.example.product_store.kafka;

import com.example.product_store.kafka.enums.InventoryStatus;
import com.example.product_store.kafka.enums.PaymentStatus;
import com.example.product_store.kafka.events.InventoryCompletedEvent;
import com.example.product_store.kafka.events.PaymentCompletedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaEvents {
    private String orderId;
    private PaymentCompletedEvent paymentCompletedEvent;
    private InventoryCompletedEvent inventoryCompletedEvent;

    public SagaEvents(String orderId) {
        this.orderId = orderId;
    }

    public boolean isBothCompletedSuccessfully(){
        return paymentCompletedEvent.getPaymentStatus() == PaymentStatus.SUCCESS &&
                inventoryCompletedEvent.getStatus() == InventoryStatus.SUCCESS;
    }

    public PaymentStatus getPaymentStatus(){
        return paymentCompletedEvent.getPaymentStatus();
    }

    public InventoryStatus getInventoryStatus(){
        return inventoryCompletedEvent.getStatus();
    }

}

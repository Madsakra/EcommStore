package com.example.product_store.order.service.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAccumulator {
    private boolean paymentAccepted;
    private boolean inventoryReserved;
}

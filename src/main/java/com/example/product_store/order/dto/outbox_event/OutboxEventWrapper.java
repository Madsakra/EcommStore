package com.example.product_store.order.dto.outbox_event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEventWrapper {
    private OutboxEventReceipt payload;
    private String eventType;
}

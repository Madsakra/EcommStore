package com.example.product_store.order.dto.outbox_event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// FOR KAFKA CONNECT / DEBEZIUM
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEventDTO {
    private String id;
    private String aggregate_type;
    private String aggregate_id;
    private String type;
    private String payload; // This is the raw JSON string
}

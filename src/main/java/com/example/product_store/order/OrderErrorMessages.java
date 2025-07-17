package com.example.product_store.order;

import lombok.Getter;

@Getter
public enum OrderErrorMessages {
    EMPTY_KAFKA_MESSAGE("Current Kafka Message Is Null Or Empty"),
    ORDER_PAYLOAD_MALFORMED("Current Order's payload is malformed"),
    WRONG_KAFKA_EVENT("Received the wrong type of event for this service");

    private final String message;
    OrderErrorMessages(String message) {
        this.message = message;
}

}
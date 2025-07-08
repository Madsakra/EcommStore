package com.example.product_store.order.util;

import com.example.product_store.order.dto.outbox_event.OrderCreatedPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventDTO;
import com.example.product_store.order.dto.outbox_event.OutboxEventWrapper;
import com.example.product_store.order.exceptions.OrderPayloadMalformedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class OutboxEventUtil {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void orderCreatedPayloadValidator(OrderCreatedPayload orderCreated){
        // CHECK THE PAYLOAD FOR ANY ERRORS
        if (orderCreated.getOrderId() == null || orderCreated.getOrderId().isEmpty()) {
            logger.warn("Current order payload has no order id");
            throw new OrderPayloadMalformedException(
                    "Current order does not have an order id tied to it");
        }

        if (orderCreated.getCustomerId() == null
                || orderCreated.getCustomerId().isEmpty()) {
            logger.warn("Current user :{}, is null / empty", orderCreated.getCustomerId());
            throw new OrderPayloadMalformedException(
                    "Current order does not have a customer tied to it");
        }

        if (orderCreated.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            logger.warn(
                    "Order has a negative sum tied to it :{}", orderCreated.getTotalPrice());
            throw new OrderPayloadMalformedException(
                    "Current order's tabulated costs is negative.");
        }
    }

    public static OutboxEventDTO extractOutboxEvent(String message) throws JsonProcessingException {
        // 1. Parse debezium outer json wrapper
        OutboxEventWrapper wrapper =
                objectMapper.readValue(message, OutboxEventWrapper.class);
        // 2. Extract "after" field (actual outbox row)
        OutboxEventDTO eventDTO = wrapper.getPayload().getAfter();
        return eventDTO;
    }


}

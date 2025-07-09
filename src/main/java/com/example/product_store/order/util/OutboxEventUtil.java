package com.example.product_store.order.util;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
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

    public static void orderCreatedPayloadValidator(OutboxEventReceipt outboxEventReceipt){
        // CHECK THE PAYLOAD FOR ANY ERRORS
        if (outboxEventReceipt.getOrderId() == null || outboxEventReceipt.getOrderId().isEmpty()) {
            logger.warn("Current order payload has no order id");
            throw new OrderPayloadMalformedException(
                    "Current order does not have an order id tied to it");
        }

        if (outboxEventReceipt.getCustomerId() == null
                || outboxEventReceipt.getCustomerId().isEmpty()) {
            logger.warn("Current user :{}, is null / empty", outboxEventReceipt.getCustomerId());
            throw new OrderPayloadMalformedException(
                    "Current order does not have a customer tied to it");
        }

        if (outboxEventReceipt.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            logger.warn(
                    "Order has a negative sum tied to it :{}", outboxEventReceipt.getTotalPrice());
            throw new OrderPayloadMalformedException(
                    "Current order's tabulated costs is negative.");
        }
    }

    public static OutboxEventReceipt extractOutboxEvent(String message) throws JsonProcessingException {
        // 1. Parse debezium outer json wrapper
        OutboxEventWrapper wrapper =
                objectMapper.readValue(message, OutboxEventWrapper.class);
        // 2. Extract the payload field
        OutboxEventReceipt outboxEventReceipt = wrapper.getPayload();
        // VALIDATE RIGHT AWAY BEFORE SENDING IT TO SERVICE
        orderCreatedPayloadValidator(outboxEventReceipt);
        outboxEventReceipt.setEventType(wrapper.getEventType());
        return outboxEventReceipt;
    }


}

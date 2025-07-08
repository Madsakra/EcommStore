package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.outbox_event.OutboxEventDTO;
import com.example.product_store.order.model.Order;
import com.example.product_store.order.repository.OrderRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class OrderCompletionService {


    private final Logger logger = LoggerFactory.getLogger(OrderCompletionService.class);

    private final OrderRepository orderRepository;
    private final ConcurrentHashMap<String, EventAccumulator> orderStatusMap = new ConcurrentHashMap<>();

    public OrderCompletionService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "store.product_store.outbox_event",
            groupId = "order-completion-consumer")
    public void execute(String message){
        if (message == null || message.isBlank()) {
            logger.warn("Received null or empty Kafka message, skipping.");
            return;
        }
        try{
            // Extract "after" field (actual outbox row)
            OutboxEventDTO event = OutboxEventUtil.extractOutboxEvent(message);
            String orderid = event.getAggregate_id();
            String type = event.getType();

            logger.info("Received event for order {}: {}",orderid,type);

            orderStatusMap.putIfAbsent(orderid,new EventAccumulator());
            EventAccumulator accumulator = orderStatusMap.get(orderid);

            if ("PaymentAccepted".equals(type)) {
                accumulator.setPaymentAccepted(true);
            } else if ("InventoryReserved".equals(type)) {
                accumulator.setInventoryReserved(true);
            }


            if (accumulator.isPaymentAccepted() && accumulator.isInventoryReserved()){

                Optional<Order> orderOptional = orderRepository.findById(orderid);

                if (orderOptional.isPresent())
                {
                    Order order = orderOptional.get();
                    order.setOrderStatus("Success");
                    order.setMessage("Order processing completed successfully");
                    Order savedOrder = orderRepository.save(order);
                    logger.info("Order completed and updated status: {}",savedOrder);

                }

                // Remove both from status map
                orderStatusMap.remove(orderid);
            }


        }
        catch (Exception ex){
            logger.warn("Order Completion failed due to the following reason: {}",ex.getMessage());
        }

    }

}

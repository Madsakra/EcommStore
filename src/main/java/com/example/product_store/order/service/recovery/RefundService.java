package com.example.product_store.order.service.recovery;

import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.exceptions.WalletNotFoundException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.WalletRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class RefundService {

    private final Logger logger = LoggerFactory.getLogger(RefundService.class);

    private final WalletRepository walletRepository;

    public RefundService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    @KafkaListener(topics = "payment-refund.events",groupId = "payment-refund-consumer")
    public void execute(String message) throws JsonProcessingException {
        if (message == null || message.isBlank()) {
            logger.warn("Received null or empty Kafka message, skipping.");
            throw new EmptyKafkaMessageException("The current kafka message is empty");
        }
            // Parse the message from debezium
            OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);

                // THE REQUIRED MESSAGE TYPE FOR THIS SERVICE
                String messageType = "InventoryFailed";

                // ONLY PICK UP ORDER CREATED EVENTS
                if (messageType.equals(receipt.getEventType())){
                    // No need to check, already done in util class
                    String customerId = receipt.getCustomerId();
                    BigDecimal subtractedValue = receipt.getTotalPrice();
                    Optional<Wallet> walletOptional = walletRepository.findByClientId(customerId);

                    if (walletOptional.isEmpty())
                    {
                        logger.warn("Current customer id :{} does not have a wallet in db",customerId);
                        throw new WalletNotFoundException("Current customer Id"+ customerId + "does not have a wallet in the db");
                    }

                    Wallet wallet = walletOptional.get();
                    wallet.setBalance(wallet.getBalance().add(subtractedValue));
                    Wallet savedWallet = walletRepository.save(wallet);
                    logger.info("Payment has been refunded to user :{}. Saved wallet info :{}",customerId,savedWallet);
                }



    }



}



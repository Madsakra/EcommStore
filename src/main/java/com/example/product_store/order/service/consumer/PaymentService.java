package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.outbox_event.OrderCreatedPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventDTO;
import com.example.product_store.order.dto.outbox_event.PaymentCompletedPayload;
import com.example.product_store.order.exceptions.InsufficientBalanceException;
import com.example.product_store.order.exceptions.WalletNotFoundException;
import com.example.product_store.order.model.OutboxEvent;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.repository.WalletRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  public static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final WalletRepository walletRepository;
  private final OutboxRepository outboxRepository;

  public PaymentService(
      WalletRepository walletRepository, OutboxRepository outboxRepository) {
    this.walletRepository = walletRepository;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @KafkaListener(
      topics = "store.product_store.outbox_event",
      groupId = "payment-service-consumer")
  public void execute(String message) {
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, skipping.");
      return;
    }
    try {
      // Extract "after" field (actual outbox row)
      OutboxEventDTO eventDTO = OutboxEventUtil.extractOutboxEvent(message);
      String messageType = "OrderCreated";

      if (Objects.equals(eventDTO.getType(), messageType)) {
        // Parse order payload
        OrderCreatedPayload orderCreated =
            objectMapper.readValue(eventDTO.getPayload(), OrderCreatedPayload.class);
        logger.info(
            "Order id is :{}, belongs to client:{}. Total price of order is :{}",
            orderCreated.getOrderId(),
            orderCreated.getCustomerId(),
            orderCreated.getTotalPrice());

        // Validate
        OutboxEventUtil.orderCreatedPayloadValidator(orderCreated);

        // fetch the wallet from db
        Optional<Wallet> walletOptional =
            walletRepository.findByClientId(orderCreated.getCustomerId());

        if (walletOptional.isEmpty()) {
          logger.warn(
              "This user :{} does not have a wallet in the db",
              orderCreated.getCustomerId());
          throw new WalletNotFoundException(
              "This user"
                  + orderCreated.getCustomerId()
                  + "does not have a wallet in the store");
        }

        Wallet wallet = walletOptional.get();
        if (wallet.getBalance().compareTo(orderCreated.getTotalPrice()) < 0) {
          logger.warn(
              "Wallet balance is insufficient, current amount is :{}",
              wallet.getBalance());
          throw new InsufficientBalanceException("Wallet has insufficient funds");
        }

        // IF CHECK ABOVE PASSES, SUBTRACT FROM WALLET
        wallet.setBalance(wallet.getBalance().subtract(orderCreated.getTotalPrice()));
        // SAVE THE WALLET IN DB
        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Saved wallet balance :{}", savedWallet.getBalance());

        // Create event
        PaymentCompletedPayload payload =
            new PaymentCompletedPayload(
                orderCreated.getOrderId(),
                "PaymentAccepted",
                orderCreated.getTotalPrice());

        // Insert into outbox
        OutboxEvent outboxEvent =
            new OutboxEvent(
                null,
                "Payment",
                orderCreated.getOrderId(),
                "PaymentAccepted",
                objectMapper.writeValueAsString(payload));
        outboxRepository.save(outboxEvent);
      }

    } catch (Exception e) {
      logger.warn("Encountered exception, throwing it: {}", e.getMessage());
    }
  }
}

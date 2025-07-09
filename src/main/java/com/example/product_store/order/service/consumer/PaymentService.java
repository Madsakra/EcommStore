package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.InsufficientBalanceException;
import com.example.product_store.order.exceptions.WalletNotFoundException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.repository.WalletRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  @KafkaListener(topics = "order.events", groupId = "payment-service-consumer")
  public void execute(String message) {
    logger.info("Received message from kafka :{}", message);
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, skipping.");
      return;
    }

    try {
      // parse the debezium kafka message
      OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);
      String messageType = "OrderCreated";

      // PASS THE PAYLOAD TO COMPLETION / FAILURE
      EventPayload payload = new EventPayload(receipt);

      if (messageType.equals(receipt.getEventType())) {
        // Validate the parsed message
        OutboxEventUtil.orderCreatedPayloadValidator(receipt);
        // fetch the wallet from db
        Optional<Wallet> walletOptional =
            walletRepository.findByClientId(receipt.getCustomerId());
        if (walletOptional.isEmpty()) {
          logger.warn(
              "This user :{} does not have a wallet in the db", receipt.getCustomerId());

          Outbox outbox =
              new Outbox(
                  null,
                  "order-completed",
                  receipt.getOrderId(),
                  "PaymentDenied",
                  objectMapper.writeValueAsString(payload));
          outboxRepository.save(outbox);

          throw new WalletNotFoundException(
              "This user"
                  + receipt.getCustomerId()
                  + "does not have a wallet in the store");
        }

        Wallet wallet = walletOptional.get();
        if (wallet.getBalance().compareTo(receipt.getTotalPrice()) < 0) {
          logger.warn(
              "Wallet balance is insufficient, current amount is :{}",
              wallet.getBalance());
          Outbox outbox =
              new Outbox(
                  null,
                  "order-completed",
                  receipt.getOrderId(),
                  "PaymentDenied",
                  objectMapper.writeValueAsString(payload));
          outboxRepository.save(outbox);
          throw new InsufficientBalanceException("Wallet has insufficient funds");
        }

        // IF CHECK ABOVE PASSES, SUBTRACT FROM WALLET
        wallet.setBalance(wallet.getBalance().subtract(receipt.getTotalPrice()));
        // SAVE THE WALLET IN DB
        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Saved wallet balance :{}", savedWallet.getBalance());

        // SAVE ORDER COMPLETION
        Outbox outbox =
            new Outbox(
                null,
                "order-completed",
                receipt.getOrderId(),
                "PaymentAccepted",
                objectMapper.writeValueAsString(payload));
        outboxRepository.save(outbox);
      }

    } catch (Exception e) {
      logger.warn("Encountered exception, throwing it: {}", e.getMessage());
    }
  }
}

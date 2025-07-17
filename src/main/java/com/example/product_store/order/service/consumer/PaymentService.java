package com.example.product_store.order.service.consumer;

import com.example.product_store.order.dto.outbox_event.EventPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventReceipt;
import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.repository.WalletRepository;
import com.example.product_store.order.util.OutboxEventUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  public PaymentService(WalletRepository walletRepository, OutboxRepository outboxRepository) {
    this.walletRepository = walletRepository;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @KafkaListener(topics = "order.events", groupId = "payment-service-consumer")
  public void execute(String message) throws JsonProcessingException {

    // CHECK IF MESSAGE IS BLANK OR NULL
    if (message == null || message.isBlank()) {
      logger.warn("Received null or empty Kafka message, throwing empty kafka message exception.");
      throw new EmptyKafkaMessageException();
    }

    // parse the debezium kafka message
    OutboxEventReceipt receipt = OutboxEventUtil.extractOutboxEvent(message);
    logger.info("Parsed kafka message from debezium: {}", receipt);

    // SHORT CIRCUIT EARLY IF EVENT TYPE IS NOT RIGHT
    if (!"OrderCreated".equals(receipt.getEventType())) {
      return;
    }

    // PAYLOAD FOR COMPLETION / FAILURE EVENT
    EventPayload payload = new EventPayload(receipt);
    // Validate the parsed message
    OutboxEventUtil.orderCreatedPayloadValidator(receipt);

    // fetch the wallet from db
    Optional<Wallet> walletOptional = walletRepository.findByClientId(receipt.getCustomerId());

    // IF THE USER DOESN'T HAVE A WALLET IN DB
    if (walletOptional.isEmpty()) {
      logger.warn("This user :{} does not have a wallet in the db", receipt.getCustomerId());
      sendOutboxEvent("PaymentDenied", receipt.getOrderId(), payload);
      return;
    }

    Wallet wallet = walletOptional.get();

    // DLQ
    // IF THE USER'S WALLET HAS INSUFFICIENT FUNDS
    if (wallet.getBalance().compareTo(receipt.getTotalPrice()) < 0) {
      logger.warn("Wallet balance is insufficient, current amount is :{}", wallet.getBalance());
      sendOutboxEvent("PaymentDenied", receipt.getOrderId(), payload);
      return;
    }

    // IF CHECK ABOVE PASSES, SUBTRACT FROM WALLET
    wallet.setBalance(wallet.getBalance().subtract(receipt.getTotalPrice()));

    // SAVE THE WALLET IN DB
    Wallet savedWallet = walletRepository.save(wallet);
    logger.info("Saved wallet balance :{}", savedWallet.getBalance());

    // SAVE ORDER COMPLETION
    sendOutboxEvent("PaymentAccepted", receipt.getOrderId(), payload);
  }

  // send outbox events through this method
  private void sendOutboxEvent(String eventType, String orderId, EventPayload payload) throws JsonProcessingException {
    Outbox outbox = new Outbox(null, "order-completed", orderId, eventType, objectMapper.writeValueAsString(payload));
    Outbox savedOutbox = outboxRepository.save(outbox);
    logger.info("Successfully saved outbox event :{}", savedOutbox);
  }
}

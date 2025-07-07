package com.example.product_store.order.service.consumer;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.order.dto.outbox_event.OrderCreatedPayload;
import com.example.product_store.order.dto.outbox_event.OutboxEventDTO;
import com.example.product_store.order.dto.outbox_event.OutboxEventWrapper;
import com.example.product_store.order.exceptions.InsufficientBalanceException;
import com.example.product_store.order.exceptions.OrderPayloadMalformedException;
import com.example.product_store.order.exceptions.WalletNotFoundException;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

  public static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final WalletRepository walletRepository;

    public PaymentService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    @Transactional
    @KafkaListener(
      topics = "store.product_store.outbox_event",
      groupId = "payment-service-consumer")
  public void execute(String message) {
    try {
      // 1. Parse debezium outer json wrapper
      OutboxEventWrapper wrapper =
          objectMapper.readValue(message, OutboxEventWrapper.class);
      // 2. Extract "after" field (actual outbox row)
      OutboxEventDTO eventDTO = wrapper.getPayload().getAfter();
      // Log the metadata
      logger.info("Parsed message from kafka: {}", eventDTO);

      // Parse order payload
      OrderCreatedPayload orderCreated =
          objectMapper.readValue(eventDTO.getPayload(), OrderCreatedPayload.class);
      logger.info(
          "Order id is :{}, belongs to client:{}. Total price of order is :{}",
          orderCreated.getOrderId(),
          orderCreated.getCustomerId(),
          orderCreated.getTotalPrice());

        // CHECK THE PAYLOAD FOR ANY ERRORS
        if (orderCreated.getOrderId() == null || orderCreated.getOrderId().isEmpty()){
            logger.warn("Current order payload has no order id");
            throw new OrderPayloadMalformedException("Current order does not have an order id tied to it");
        }

        if (orderCreated.getCustomerId() == null || orderCreated.getCustomerId().isEmpty()) {
            logger.warn("Current user :{}, is null / empty",orderCreated.getCustomerId());
            throw new OrderPayloadMalformedException("Current order does not have a customer tied to it");
        }

        if (orderCreated.getTotalPrice().compareTo(BigDecimal.ZERO)<0){
            logger.warn("Order has a negative sum tied to it :{}",orderCreated.getTotalPrice());
            throw new OrderPayloadMalformedException("Current order's tabulated costs is negative.");
        }


        // fetch the wallet from db
        Optional<Wallet> walletOptional = walletRepository.findByClientId(orderCreated.getCustomerId());

        if (walletOptional.isEmpty())
        {
            logger.warn("This user :{} does not have a wallet in the db",orderCreated.getCustomerId());
            throw new WalletNotFoundException("This user" + orderCreated.getCustomerId() + "does not have a wallet in the store");
        }

        Wallet wallet = walletOptional.get();
       if (wallet.getBalance().compareTo(orderCreated.getTotalPrice()) <0){
           logger.warn("Wallet balance is insufficient, current amount is :{}",wallet.getBalance());
           throw new InsufficientBalanceException("Wallet has insufficient funds");
       }

       // IF CHECK ABOVE PASSES, SUBTRACT FROM WALLET
       wallet.setBalance(wallet.getBalance().subtract(orderCreated.getTotalPrice()));
       // SAVE THE WALLET IN DB
        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Saved wallet balance :{}",savedWallet.getBalance());



    } catch (Exception e) {
      logger.warn("Encountered exception, throwing it: {}", e.getMessage());
    }
  }
}

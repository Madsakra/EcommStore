package com.example.product_store.order.service.consumers;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.order.enums.PaymentStatus;
import com.example.product_store.order.events.PaymentCompletedEvent;
import com.example.product_store.order.events.StartPaymentEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StartPaymentService {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final AccountRepository accountRepository;
  public static final Logger logger = LoggerFactory.getLogger(StartPaymentService.class);

  public StartPaymentService(
      KafkaTemplate<String, Object> kafkaTemplate, AccountRepository accountRepository) {
    this.kafkaTemplate = kafkaTemplate;
    this.accountRepository = accountRepository;
  }

  // CONSUME PAYMENT EVENT
  @KafkaListener(topics = "payment-commands", groupId = "saga-group")
  public void processPayment(StartPaymentEvent startPaymentEvent) {
    logger.info(
        "Received Payment Event at Payment service: {}. Time: {}",
        startPaymentEvent,
        LocalDateTime.now());

    // retrieve the current user as an Account Object
    Optional<Account> accountOptional =
        accountRepository.findById(startPaymentEvent.getCustomerId());

    PaymentStatus status;
    String message;

    // If account is not found
    if (accountOptional.isEmpty()) {
      status = PaymentStatus.DENIED;
      message = "Payment failed: customer not found.";
    } else {
      Account account = accountOptional.get();
      BigDecimal balance = account.getBalance();
      BigDecimal totalPrice = startPaymentEvent.getTotalPrice();

      // If account is null or balance is <= 0
      // set the status of the payment to denied
      if (balance == null || balance.compareTo(totalPrice) <= 0) {
        status = PaymentStatus.DENIED;
        message = "Payment failed: insufficient balance.";
      } else {
        account.setBalance(balance.subtract(totalPrice));
        accountRepository.save(account);
        // deduction successful
        // set status to success
        message = "Payment Success.";
        status = PaymentStatus.SUCCESS;
      }
    }
    logger.info("Payment processing status: {}", status);
    PaymentCompletedEvent responseEvent =
        new PaymentCompletedEvent(startPaymentEvent, status, message);
    kafkaTemplate.send("payment-events", responseEvent);
  }
}

package com.example.product_store.order.service.consumers;

import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.order.events.PaymentCompletedEvent;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RollBackPaymentService {

  private final AccountRepository accountRepository;
  private final Logger logger = LoggerFactory.getLogger(RollBackPaymentService.class);

  public RollBackPaymentService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @KafkaListener(topics = "inventory-failed", groupId = "saga-group")
  public Void execute(PaymentCompletedEvent paymentCompletedEvent) {

    Optional<Account> accountOptional =
            accountRepository.findById(paymentCompletedEvent.getCustomerId());
    if (accountOptional.isPresent()) {
      Account account = accountOptional.get();
      account.setBalance(
              account.getBalance().add(paymentCompletedEvent.getBalanceReduced()));
      accountRepository.save(account);
      logger.info(
              "Refunded user: {} ${}",
              account.getId(),
              paymentCompletedEvent.getBalanceReduced());
      return null;
    } else {
      logger.warn("Failure to roll back payment as client account could not be found.");
      throw new AccountNotFoundException(
              "Failure to roll back payment as the client account could not be found");
    }

  }
}

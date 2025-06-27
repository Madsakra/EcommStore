package com.example.product_store.kafka.service;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.kafka.events.PaymentCompletedEvent;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RollBackPayment {

  private final AccountRepository accountRepository;
  private final Logger logger = LoggerFactory.getLogger(RollBackPayment.class);

  public RollBackPayment(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Void execute(PaymentCompletedEvent paymentCompletedEvent) {

    Optional<Account> accountOptional =
        accountRepository.findById(paymentCompletedEvent.getCustomerId());
    if (accountOptional.isPresent()) {
      Account account = accountOptional.get();
      account.setBalance(
          account.getBalance().add(paymentCompletedEvent.getBalanceReduced()));
      accountRepository.save(account);
      logger.info("Refunded user: {} ${}", account.getId(), account.getBalance());
    }

    return null;
  }
}

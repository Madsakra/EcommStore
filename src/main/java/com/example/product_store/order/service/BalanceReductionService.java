package com.example.product_store.order.service;

import com.example.product_store.authentication.model.Account;
import com.example.product_store.order.OrderCreationRequest;
import com.example.product_store.kafka.exceptions.InsufficientBalanceException;
import com.example.product_store.store.product.model.Product;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BalanceReductionService {

  public Logger logger = LoggerFactory.getLogger(BalanceReductionService.class);

    public BigDecimal execute(Account account, Product product, OrderCreationRequest request) {
    // GET THE FULL COSTS FOR EACH BATCH OF ITEMS
    // PAYLOAD QUANTITY * THE PRODUCT PRICE IN DB
    BigDecimal batchCost = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
    logger.info("Full Costs of item is : {}, in BalanceReductionService", batchCost);

    // CHECK IF THE BALANCE IS SUFFICIENT FOR DEDUCTION
    if (account.getBalance().compareTo(batchCost) < 0) {
      logger.warn("Insufficient balance, left : ${}, rolling back with transaction", account.getBalance());
      throw new InsufficientBalanceException("Insufficient balance to order the product, throwing Insufficient Balance exception im BalanceReductionService");
    }

    // SUBTRACT BALANCE AND STOCK
    account.setBalance(account.getBalance().subtract(batchCost));
    logger.info("Account Balance After Subtraction: {}, in Balance Reduction Service", account.getBalance());


    return batchCost;
  }
}

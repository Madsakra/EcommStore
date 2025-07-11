package com.example.order.consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.model.Outbox;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.OutboxRepository;
import com.example.product_store.order.repository.WalletRepository;
import com.example.product_store.order.service.consumer.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

  @Mock private ObjectMapper objectMapper;

  @Mock private WalletRepository walletRepository;

  @Mock private OutboxRepository outboxRepository;

  @InjectMocks private PaymentService paymentService;

  @Test
  void testExecute_withEmptyKafkaMessage_shouldThrowException() {
    String message = "";
    EmptyKafkaMessageException exception =
        assertThrows(EmptyKafkaMessageException.class, () -> paymentService.execute(message));
  }

  @Test
  void testExecute_withNullKafkaMessage_shouldThrowException() {
    EmptyKafkaMessageException exception =
        assertThrows(EmptyKafkaMessageException.class, () -> paymentService.execute(null));
  }

  @Test
  void testExecute_successPayment() throws JsonProcessingException {
    // GIVEN
    String clientId = "user123";
    String orderId = "order123";

    // SET UP WALLET WITH ENOUGH BALANCE
    Wallet wallet = new Wallet();
    wallet.setClientId(clientId);
    wallet.setBalance(BigDecimal.valueOf(100));

    // WHEN
    when(walletRepository.findByClientId(clientId)).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(outboxRepository.save(any(Outbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

    String kafkaMessage =
        """
        {
          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00
                      }
        }
        """;

    // ACT
    paymentService.execute(kafkaMessage);

    // THEN
    // ENSURE WALLET WAS SAVED WITH SUBTRACTED BALANCE
    ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
    verify(walletRepository).save(walletCaptor.capture());
    assertEquals(new BigDecimal("50.00"), walletCaptor.getValue().getBalance());

    // Ensure PaymentAccepted event was saved
    ArgumentCaptor<Outbox> outboxCaptor = ArgumentCaptor.forClass(Outbox.class);
    verify(outboxRepository).save(outboxCaptor.capture());

    Outbox savedOutbox = outboxCaptor.getValue();
    assertEquals("PaymentAccepted", savedOutbox.getType());
    assertEquals("order-completed", savedOutbox.getAggregateType());
    assertEquals(orderId, savedOutbox.getAggregateId());
  }

  @Test
  void testExecute_walletNotFound_shouldThrowException() throws JsonProcessingException {
    // GIVEN
    String clientId = "user123";
    String orderId = "order123";

    // SET UP WALLET WITH ENOUGH BALANCE
    Wallet wallet = new Wallet();
    wallet.setClientId(clientId);
    wallet.setBalance(BigDecimal.valueOf(100));

    // WHEN
    when(walletRepository.findByClientId(clientId)).thenReturn(Optional.empty());
    String kafkaMessage =
        """
        {
          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 50.00
                      }
        }
        """;
    paymentService.execute(kafkaMessage);

    // VERIFY OUTBOX EVENT SENT (Failure)
    ArgumentCaptor<Outbox> outboxCaptor = ArgumentCaptor.forClass(Outbox.class);
    verify(outboxRepository).save(outboxCaptor.capture());
    Outbox savedOutbox = outboxCaptor.getValue();
    assertEquals("PaymentDenied", savedOutbox.getType());
    assertEquals(orderId, savedOutbox.getAggregateId());
  }

  @Test
  void testExecute_insufficientBalance_shouldThrowException() throws JsonProcessingException {
    // GIVEN
    String clientId = "user123";
    String orderId = "order123";

    // SET UP WALLET WITH ENOUGH BALANCE
    Wallet wallet = new Wallet();
    wallet.setClientId(clientId);
    wallet.setBalance(BigDecimal.valueOf(100));

    // WHEN
    when(walletRepository.findByClientId(clientId)).thenReturn(Optional.of(wallet));
    String kafkaMessage =
        """
        {
          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
                      }
        }
        """;

    paymentService.execute(kafkaMessage);
    // VERIFY OUTBOX EVENT SENT
    ArgumentCaptor<Outbox> outboxCaptor = ArgumentCaptor.forClass(Outbox.class);
    verify(outboxRepository).save(outboxCaptor.capture());
    Outbox savedOutbox = outboxCaptor.getValue();
    assertEquals("PaymentDenied", savedOutbox.getType());
    assertEquals(orderId, savedOutbox.getAggregateId());
  }

  @Test
  void testExecute_malformedJson_shouldThrowJsonProcessingException() {
    String kafkaMessage =
        """

          "eventType": "OrderCreated",
          "payload": {
            "customerId": "user123",
            "orderId": "order123",
            "totalPrice": 150.00
                      }
        }
        """;

    JsonProcessingException exception =
        assertThrows(JsonProcessingException.class, () -> paymentService.execute(kafkaMessage));
  }
}

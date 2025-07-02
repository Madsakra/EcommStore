package com.example.orders.consumers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.order.enums.PaymentStatus;
import com.example.product_store.order.events.PaymentCompletedEvent;
import com.example.product_store.order.events.StartPaymentEvent;
import com.example.product_store.order.service.consumers.StartPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StartPaymentServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private StartPaymentService startPaymentService;

    @Test
    void shouldSendSuccessfulEvent_whenBalanceIsSufficient(){
        // Given
        StartPaymentEvent event = new StartPaymentEvent("order-1","user-1", BigDecimal.valueOf(1000), LocalDateTime.now(), PaymentStatus.PROCESSING);
        Account account = new Account();
        account.setId("user-1");
        account.setBalance(BigDecimal.valueOf(10000));

        // WHEN
        when(accountRepository.findById("user-1")).thenReturn(Optional.of(account));

        // ACT
        startPaymentService.processPayment(event);

        // ASSERT
        ArgumentCaptor<PaymentCompletedEvent> captor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(kafkaTemplate).send(eq("payment-events"),captor.capture());

        PaymentCompletedEvent sentEvent = captor.getValue();
        assertEquals(PaymentStatus.SUCCESS,sentEvent.getPaymentStatus());
        assertEquals("Payment Success.",sentEvent.getMessage());
    }

    @Test
    void shouldSendDeniedEvent_WhenUserNotFound(){
        // Given
        StartPaymentEvent event = new StartPaymentEvent("order-2","user-1", BigDecimal.valueOf(1000), LocalDateTime.now(), PaymentStatus.PROCESSING);

        // WHEN
        startPaymentService.processPayment(event);

        // ACT
        ArgumentCaptor<PaymentCompletedEvent> captor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(kafkaTemplate).send(eq("payment-events"),captor.capture());

        // INSTEAD OF THROWING EXCEPTION
        // KAFKA SENDS FAILED EVENTS
        PaymentCompletedEvent sendEvent = captor.getValue();
        assertEquals(PaymentStatus.DENIED,sendEvent.getPaymentStatus());
        assertTrue(sendEvent.getMessage().contains("customer not found"));
    }

    // WHEN USER BALANCE IS INSUFFICIENT
    // KAFKA WILL SEND EVENT WITH FAILED STATUS
    @Test
    void shouldSendDeniedEvent_WhenInsufficientBalance(){
        // GIVEN
        // Given
        StartPaymentEvent event = new StartPaymentEvent("order-1","user-1", BigDecimal.valueOf(1000), LocalDateTime.now(), PaymentStatus.PROCESSING);
        Account account = new Account();
        account.setId("user-1");
        account.setBalance(BigDecimal.valueOf(10));

        // WHEN
        when(accountRepository.findById("user-1")).thenReturn(Optional.of(account));

        // ACT
        startPaymentService.processPayment(event);

        // ASSERT
        ArgumentCaptor<PaymentCompletedEvent> captor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(kafkaTemplate).send(eq("payment-events"), captor.capture());

        PaymentCompletedEvent sentEvent = captor.getValue();
        assertEquals(PaymentStatus.DENIED, sentEvent.getPaymentStatus());
        assertTrue(sentEvent.getMessage().contains("insufficient balance"));

    }

}

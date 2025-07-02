package com.example.orders.consumers;
import com.example.product_store.authentication.errors.AccountNotFoundException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.order.events.PaymentCompletedEvent;
import com.example.product_store.order.service.consumers.RollBackPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RollBackPaymentServiceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RollBackPaymentService rollBackPaymentService;

    @Test
    void testExecute_ShouldRefundUserSuccessfully(){
        BigDecimal reducedAmount = new BigDecimal("50.00");
        Account account = new Account();
        account.setId("user-1");
        account.setBalance(BigDecimal.valueOf(100));

        PaymentCompletedEvent event = new PaymentCompletedEvent();
        event.setCustomerId(account.getId());
        event.setBalanceReduced(reducedAmount);

        // WHEN
        when(accountRepository.findById(event.getCustomerId())).thenReturn(Optional.of(account));
        rollBackPaymentService.execute(event);

        // THEN
        assertEquals(new BigDecimal("150.00"),account.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testExecute_shouldThrowException_whenAccountNotFound(){
        // GIVEN
        PaymentCompletedEvent event = new PaymentCompletedEvent();
        event.setCustomerId("non-existent-id");
        event.setBalanceReduced(new BigDecimal("100.00"));

        when(accountRepository.findById("non-existent-id")).thenReturn(Optional.empty());
        // ASSERT AND THROW
        assertThrows(AccountNotFoundException.class,
                ()->rollBackPaymentService.execute(event));
    }


}

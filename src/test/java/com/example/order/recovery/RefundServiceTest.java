package com.example.order.recovery;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.order.exceptions.EmptyKafkaMessageException;
import com.example.product_store.order.exceptions.WalletNotFoundException;
import com.example.product_store.order.model.Wallet;
import com.example.product_store.order.repository.WalletRepository;
import com.example.product_store.order.service.recovery.RefundService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RefundServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private RefundService refundService;

    @Test
    void testExecute_refundSuccessful() throws JsonProcessingException {
        String kafkaMessage =
                """
                {
                  "eventType": "InventoryFailed",
                  "payload": {
                    "customerId": "user123",
                    "orderId": "order123",
                    "totalPrice": 50.00,
                    "orderCreationRequests": [
                      { "id": "product1", "quantity": 4 },
                      { "id": "product2", "quantity": 2 }
                    ]
                  }
                }
                """;

        Wallet expectedWallet = new Wallet("wallet-123","user123", BigDecimal.valueOf(100));
        when(walletRepository.findByClientId("user123")).thenReturn(Optional.of(expectedWallet));
        refundService.execute(kafkaMessage);

        // SINCE IT IS REFUND, ADD ON 50
        assertEquals(new BigDecimal("150.00"),expectedWallet.getBalance());
        assertEquals("user123",expectedWallet.getClientId());
        verify(walletRepository).save(expectedWallet);
    }

    @Test
    void testExecute_noWallet_shouldThrowException(){
        // GIVEN
        String kafkaMessage =
                """
                {
                  "eventType": "InventoryFailed",
                  "payload": {
                    "customerId": "user123",
                    "orderId": "order123",
                    "totalPrice": 50.00,
                    "orderCreationRequests": [
                      { "id": "product1", "quantity": 4 },
                      { "id": "product2", "quantity": 2 }
                    ]
                  }
                }
                """;

        // WHEN
        when(walletRepository.findByClientId("user123")).thenReturn(Optional.empty());

        // ACT
        WalletNotFoundException noWallet = assertThrows(WalletNotFoundException.class,()-> refundService.execute(kafkaMessage));
        assertTrue(noWallet.getMessage().contains("user123"));
    }

    @Test
    void testExecute_malformedJsonMessage_shouldThrowException(){
        // GIVEN
        String kafkaMessage =
                """
                
                  "eventType": "OrderCreated",
                  "payload": {
                    "customerId": "user123",
                    "orderId": "order123",
                    "totalPrice": 50.00,
                    "orderCreationRequests": [
                      { "id": "product1", "quantity": 4 },
                      { "id": "product2", "quantity": 2 }
                    ]
                  }
                }
                """;

        JsonProcessingException ex = assertThrows(JsonProcessingException.class,()->refundService.execute(kafkaMessage));
    }

    @Test
    void testExecute_nullMessage_shouldThrowException(){
        EmptyKafkaMessageException ex = assertThrows(EmptyKafkaMessageException.class,
                ()-> refundService.execute(null));
        assertEquals("The current kafka message is empty",ex.getMessage());
    }

    @Test
    void testExecute_emptyMessage_shouldThrowException(){
        String kafkaMessage = "";
        EmptyKafkaMessageException ex = assertThrows(EmptyKafkaMessageException.class,
                ()-> refundService.execute(kafkaMessage));
        assertEquals("The current kafka message is empty",ex.getMessage());
    }

}

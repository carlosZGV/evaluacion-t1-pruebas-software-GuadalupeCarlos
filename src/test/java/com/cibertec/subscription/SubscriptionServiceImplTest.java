package com.cibertec.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    @DisplayName("Debe suspender cuenta y lanzar excepcion si tarjeta expiro")
    void tarjetaExpiradaSuspendeCuentaYSinCobro() {
        Long id = 1L;
        Subscription sub = new Subscription(id, "ACTIVE", LocalDate.now());

        when(subscriptionRepository.findById(id)).thenReturn(sub);
        when(paymentGatewayClient.isCardValid(id)).thenReturn(false);

        assertThrows(ExpiredCardException.class, () -> 
            subscriptionService.renewSubscription(id)
        );

        verify(subscriptionRepository).updateStatus(id, "SUSPENDED");
        verify(paymentGatewayClient, never()).chargeMonthlyFee(anyLong());
        verify(subscriptionRepository, never()).updateBillingDate(anyLong(), any());
    }

    @Test
    @DisplayName("Debe renovar y extender un mes si tarjeta es valida")
    void tarjetaValidaRenuevaYExtiendeUnMes() {
        Long id = 1L;
        LocalDate hoy = LocalDate.now();
        LocalDate proximoMes = hoy.plusMonths(1);
        Subscription sub = new Subscription(id, "ACTIVE", hoy);

        when(subscriptionRepository.findById(id)).thenReturn(sub);
        when(paymentGatewayClient.isCardValid(id)).thenReturn(true);

        Subscription renovada = subscriptionService.renewSubscription(id);

        assertNotNull(renovada);
        assertEquals("ACTIVE", renovada.status());
        assertEquals(proximoMes, renovada.nextBillingDate());

        verify(paymentGatewayClient).chargeMonthlyFee(id);
        verify(subscriptionRepository).updateBillingDate(id, proximoMes);
    }
}

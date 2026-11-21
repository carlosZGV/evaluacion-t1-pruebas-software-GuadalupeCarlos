package com.cibertec.loyalty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoyaltyServiceImplTest {

    @Mock
    private LoyaltyRepository loyaltyRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    @Test
    @DisplayName("Debe lanzar excepcion y no registrar puntos si el monto es invalido")
    void montoInvalidoNoRegistraPuntos() {
        Long userId = 1L;
        BigDecimal monto = BigDecimal.ZERO;

        assertThrows(IllegalArgumentException.class, () -> 
            loyaltyService.calculateAndAssignPoints(userId, monto)
        );

        verify(loyaltyRepository, never()).addPoints(anyLong(), anyInt());
        verify(loyaltyRepository, never()).isVipUser(anyLong());
    }

    @Test
    @DisplayName("Debe duplicar puntos cuando el usuario es VIP")
    void usuarioVipRecibePuntosDobles() {
        Long userId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        when(loyaltyRepository.isVipUser(userId)).thenReturn(true);

        LoyaltyResponse response = loyaltyService.calculateAndAssignPoints(userId, monto);

        assertNotNull(response);
        assertEquals(200, response.pointsEarned());
        assertTrue(response.isVip());
        verify(loyaltyRepository, times(1)).addPoints(userId, 200);
    }

    @Test
    @DisplayName("Debe asignar puntos normales cuando el usuario es regular")
    void usuarioRegularRecibePuntosNormales() {
        Long userId = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        when(loyaltyRepository.isVipUser(userId)).thenReturn(false);

        LoyaltyResponse response = loyaltyService.calculateAndAssignPoints(userId, monto);

        assertNotNull(response);
        assertEquals(100, response.pointsEarned());
        assertFalse(response.isVip());
        verify(loyaltyRepository, times(1)).addPoints(userId, 100);
    }
}

package com.cibertec.loyalty.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoyaltyPointsCalculatorTest {

    private LoyaltyPointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LoyaltyPointsCalculator();
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el monto es nulo o menor igual a cero")
    void montoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculateAccumulatedPoints(null, "REGULAR")
        );

        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculateAccumulatedPoints(BigDecimal.ZERO, "REGULAR")
        );

        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculateAccumulatedPoints(new BigDecimal("-10"), "REGULAR")
        );
    }

    @Test
    @DisplayName("Debe calcular puntos para cliente regular")
    void clienteRegularCalculaPuntos() {
        BigDecimal monto = new BigDecimal("100.50");
        int puntos = calculator.calculateAccumulatedPoints(monto, "REGULAR");
        assertEquals(100, puntos);
    }

    @Test
    @DisplayName("Debe duplicar puntos para cliente VIP")
    void clienteVipDuplicaPuntos() {
        BigDecimal monto = new BigDecimal("100.50");
        int puntos = calculator.calculateAccumulatedPoints(monto, "VIP");
        assertEquals(201, puntos);
    }
}

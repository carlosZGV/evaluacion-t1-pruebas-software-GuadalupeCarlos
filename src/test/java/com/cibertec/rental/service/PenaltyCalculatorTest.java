package com.cibertec.rental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PenaltyCalculatorTest {

    private PenaltyCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PenaltyCalculator();
    }

    @Test
    @DisplayName("Debe lanzar excepcion si los dias son negativos")
    void diasNegativosLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> 
            calculator.calculateLatePenalty(-1)
        );
    }

    @Test
    @DisplayName("Debe retornar cero cuando no hay retraso")
    void ceroDiasSinPenalizacion() {
        int resultado = calculator.calculateLatePenalty(0);
        assertEquals(0, resultado);
    }

    @Test
    @DisplayName("Debe cobrar tarifa base si el retraso es menor o igual a 7 dias")
    void retrasoLeveCalculaTarifaBase() {
        int resultado = calculator.calculateLatePenalty(5);
        assertEquals(75, resultado);
    }

    @Test
    @DisplayName("Debe aplicar tope maximo de 300 ante retraso prolongado")
    void retrasoProlongadoAplicaTopeMaximo() {
        int resultado = calculator.calculateLatePenalty(20);
        assertEquals(300, resultado);
    }
}

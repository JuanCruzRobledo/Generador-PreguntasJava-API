package org.jcr.generadorpreguntasjava.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Tematica del dominio.
 */
@DisplayName("Tests de la entidad Tematica")
class TematicaTest {
    
    @ParameterizedTest
    @MethodSource("proveerCasosNormalizacion")
    @DisplayName("Debe normalizar nombres correctamente")
    void debeNormalizarNombresCorrectamente(String entrada, String esperado) {
        // When
        String resultado = Tematica.normalizarNombre(entrada);
        
        // Then
        assertEquals(esperado, resultado);
    }
    
    static Stream<Arguments> proveerCasosNormalizacion() {
        return Stream.of(
            Arguments.of("Arrays", "arrays"),
            Arguments.of("BUCLES", "bucles"),
            Arguments.of("Programación", "programacion"),
            Arguments.of("Métodos", "metodos"),
            Arguments.of("  Espacios  ", "espacios"),
            Arguments.of("Condicionales-IF", "condicionales-if"),
            Arguments.of("Clases y Objetos", "clases y objetos")
        );
    }
    
    @Test
    @DisplayName("Debe incrementar contador de usos correctamente")
    void debeIncrementarContadorDeUsosCorrectamente() {
        // Given
        Tematica tematica = new Tematica("arrays", 2, LocalDateTime.now());
        
        // When
        Tematica tematicaIncrementada = tematica.incrementarUso();
        
        // Then
        assertEquals(3, tematicaIncrementada.contadorUsos());
        assertNotNull(tematicaIncrementada.timestampUltimoUso());
    }
    
    @Test
    @DisplayName("Debe crear temática con constructor simple")
    void debeCrearTematicaConConstructorSimple() {
        // When
        Tematica tematica = new Tematica("Arrays");
        
        // Then
        assertEquals("arrays", tematica.nombre());
        assertEquals(0, tematica.contadorUsos());
        assertNull(tematica.timestampUltimoUso());
    }
    
    @Test
    @DisplayName("Debe fallar validación con nombre vacío")
    void debeFallarValidacionConNombreVacio() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Tematica("");
        });
    }
    
    @Test
    @DisplayName("Debe fallar validación con nombre null")
    void debeFallarValidacionConNombreNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Tematica(null);
        });
    }
    
    @Test
    @DisplayName("Debe validar correctamente una temática válida")
    void debeValidarCorrectamenteUnaTematicaValida() {
        // Given
        Tematica tematica = new Tematica("arrays", 5, LocalDateTime.now());
        
        // When & Then
        assertDoesNotThrow(tematica::validar);
    }
    
    @Test
    @DisplayName("Debe fallar validación con contador negativo")
    void debeFallarValidacionConContadorNegativo() {
        // Given
        Tematica tematica = new Tematica(1L, "arrays", -1, LocalDateTime.now());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, tematica::validar);
    }
}

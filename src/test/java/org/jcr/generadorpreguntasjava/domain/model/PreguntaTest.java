package org.jcr.generadorpreguntasjava.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Pregunta del dominio.
 */
@DisplayName("Tests de la entidad Pregunta")
class PreguntaTest {
    
    @Test
    @DisplayName("Debe validar respuesta correcta exitosamente")
    void debeValidarRespuestaCorrectaExitosamente() {
        // Given
        Pregunta pregunta = crearPreguntaEjemplo();
        String respuestaDada = "15";
        
        // When
        boolean resultado = pregunta.validarRespuesta(respuestaDada);
        
        // Then
        assertTrue(resultado);
    }
    
    @Test
    @DisplayName("Debe fallar validación con respuesta incorrecta")
    void debeFallarValidacionConRespuestaIncorrecta() {
        // Given
        Pregunta pregunta = crearPreguntaEjemplo();
        String respuestaDada = "20";
        
        // When
        boolean resultado = pregunta.validarRespuesta(respuestaDada);
        
        // Then
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Debe obtener temática principal correctamente")
    void debeObtenerTematicaPrincipalCorrectamente() {
        // Given
        Pregunta pregunta = crearPreguntaEjemplo();
        
        // When
        Tematica tematicaPrincipal = pregunta.getTematicaPrincipal();
        
        // Then
        assertNotNull(tematicaPrincipal);
        assertEquals("arrays", tematicaPrincipal.nombre());
    }
    
    @Test
    @DisplayName("Debe obtener temáticas secundarias correctamente")
    void debeObtenerTematicasSecundariasCorrectamente() {
        // Given
        Pregunta pregunta = crearPreguntaEjemplo();
        
        // When
        List<Tematica> secundarias = pregunta.getTematicasSecundarias();
        
        // Then
        assertEquals(1, secundarias.size());
        assertEquals("bucles", secundarias.get(0).nombre());
    }
    
    @Test
    @DisplayName("Debe fallar validación si faltan opciones")
    void debeFallarValidacionSiFaltanOpciones() {
        // Given
        List<Opcion> opcionesIncompletas = List.of(
            new Opcion("Opción 1"),
            new Opcion("Opción 2")
        );
        
        Pregunta preguntaInvalida = new Pregunta(
            "public class Test {}",
            "¿Qué hace este código?",
            Dificultad.FACIL,
            "Opción 1",
            "Es una clase vacía",
            opcionesIncompletas,
            List.of(new Tematica("clases"))
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, preguntaInvalida::validar);
    }
    
    @Test
    @DisplayName("Debe fallar validación si respuesta correcta no está en opciones")
    void debeFallarValidacionSiRespuestaCorrectaNoEstaEnOpciones() {
        // Given
        List<Opcion> opciones = List.of(
            new Opcion("Opción A"),
            new Opcion("Opción B"),
            new Opcion("Opción C"),
            new Opcion("Opción D")
        );
        
        Pregunta preguntaInvalida = new Pregunta(
            "public class Test {}",
            "¿Qué hace este código?",
            Dificultad.FACIL,
            "Opción E", // No existe en las opciones
            "Es una clase vacía",
            opciones,
            List.of(new Tematica("clases"))
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, preguntaInvalida::validar);
    }
    
    private Pregunta crearPreguntaEjemplo() {
        List<Opcion> opciones = List.of(
            new Opcion("10"),
            new Opcion("15"),
            new Opcion("20"),
            new Opcion("25")
        );
        
        List<Tematica> tematicas = List.of(
            new Tematica("arrays"),
            new Tematica("bucles")
        );
        
        return new Pregunta(
            "int[] nums = {1,2,3,4,5}; int sum = 0; for(int n : nums) sum += n;",
            "¿Cuál es el valor de sum?",
            Dificultad.FACIL,
            "15",
            "La suma de 1+2+3+4+5 es 15",
            opciones,
            tematicas
        );
    }
}

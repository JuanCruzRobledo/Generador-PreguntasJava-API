package org.jcr.generadorpreguntasjava.application.service;

import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.ValidarRespuestaPort;
import org.jcr.generadorpreguntasjava.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para el servicio de aplicación PreguntaService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del PreguntaService")
class PreguntaServiceTest {
    
    @Mock
    private PreguntaRepositoryPort preguntaRepositoryPort;
    
    @Mock
    private TematicaRepositoryPort tematicaRepositoryPort;
    
    @Mock
    private GeneradorDePreguntaServicePort generadorDePreguntaServicePort;
    
    @Mock
    private PromptBuilderService promptBuilderService;
    
    private PreguntaService preguntaService;
    
    @BeforeEach
    void setUp() {
        preguntaService = new PreguntaService(
            preguntaRepositoryPort,
            tematicaRepositoryPort,
            generadorDePreguntaServicePort,
            promptBuilderService
        );
    }
    
    @Test
    @DisplayName("Debe generar pregunta exitosamente")
    void debeGenerarPreguntaExitosamente() {
        // Given
        Dificultad dificultad = Dificultad.FACIL;
        String tematicaDeseada = "arrays";
        String promptCompleto = "prompt completo";
        
        GeneradorDePreguntaServicePort.RespuestaGeneracion respuestaGeneracion = 
            new GeneradorDePreguntaServicePort.RespuestaGeneracion(
                "int[] nums = {1,2,3};",
                "¿Qué hace este código?",
                new String[]{"A", "B", "C", "D"},
                "A",
                "Explicación",
                "arrays",
                "declaracion",
                "facil"
            );
        
        Pregunta preguntaEsperada = crearPreguntaEjemplo();
        
        when(promptBuilderService.construirPromptCompleto("facil", tematicaDeseada))
            .thenReturn(promptCompleto);
        when(generadorDePreguntaServicePort.generarPregunta(promptCompleto))
            .thenReturn(respuestaGeneracion);
        when(tematicaRepositoryPort.buscarPorNombre(anyString()))
            .thenReturn(Optional.empty());
        when(tematicaRepositoryPort.guardar(any(Tematica.class)))
            .thenAnswer(invocation -> {
                Tematica t = invocation.getArgument(0);
                return t.withId(1L);
            });
        when(preguntaRepositoryPort.guardar(any(Pregunta.class)))
            .thenReturn(preguntaEsperada.withId(1L));
        
        // When
        Pregunta resultado = preguntaService.generarPregunta(dificultad, tematicaDeseada);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        verify(promptBuilderService).construirPromptCompleto("facil", tematicaDeseada);
        verify(generadorDePreguntaServicePort).generarPregunta(promptCompleto);
        verify(preguntaRepositoryPort).guardar(any(Pregunta.class));
    }
    
    @Test
    @DisplayName("Debe validar respuesta correcta exitosamente")
    void debeValidarRespuestaCorrectaExitosamente() {
        // Given
        Long preguntaId = 1L;
        String respuestaDada = "15";
        Pregunta pregunta = crearPreguntaEjemplo();
        
        when(preguntaRepositoryPort.buscarPorId(preguntaId))
            .thenReturn(Optional.of(pregunta));
        
        // When
        ValidarRespuestaPort.ResultadoValidacion resultado = 
            preguntaService.validarRespuesta(preguntaId, respuestaDada);
        
        // Then
        assertTrue(resultado.esCorrecta());
        assertEquals(pregunta.explicacion(), resultado.explicacion());
        assertEquals(pregunta.respuestaCorrecta(), resultado.respuestaCorrecta());
        verify(preguntaRepositoryPort).buscarPorId(preguntaId);
    }
    
    @Test
    @DisplayName("Debe validar respuesta incorrecta exitosamente")
    void debeValidarRespuestaIncorrectaExitosamente() {
        // Given
        Long preguntaId = 1L;
        String respuestaDada = "20";
        Pregunta pregunta = crearPreguntaEjemplo();
        
        when(preguntaRepositoryPort.buscarPorId(preguntaId))
            .thenReturn(Optional.of(pregunta));
        
        // When
        ValidarRespuestaPort.ResultadoValidacion resultado = 
            preguntaService.validarRespuesta(preguntaId, respuestaDada);
        
        // Then
        assertFalse(resultado.esCorrecta());
        assertEquals(pregunta.explicacion(), resultado.explicacion());
        assertEquals(pregunta.respuestaCorrecta(), resultado.respuestaCorrecta());
    }
    
    @Test
    @DisplayName("Debe fallar validación cuando pregunta no existe")
    void debeFallarValidacionCuandoPreguntaNoExiste() {
        // Given
        Long preguntaId = 999L;
        String respuestaDada = "A";
        
        when(preguntaRepositoryPort.buscarPorId(preguntaId))
            .thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            preguntaService.validarRespuesta(preguntaId, respuestaDada);
        });
        
        assertTrue(exception.getMessage().contains("Pregunta no encontrada"));
        verify(preguntaRepositoryPort).buscarPorId(preguntaId);
    }
    
    @Test
    @DisplayName("Debe obtener todas las preguntas exitosamente")
    void debeObtenerTodasLasPreguntasExitosamente() {
        // Given
        List<Pregunta> preguntasEsperadas = List.of(
            crearPreguntaEjemplo(),
            crearPreguntaEjemplo()
        );
        
        when(preguntaRepositoryPort.obtenerTodas())
            .thenReturn(preguntasEsperadas);
        
        // When
        List<Pregunta> resultado = preguntaService.obtenerTodasLasPreguntas();
        
        // Then
        assertEquals(2, resultado.size());
        verify(preguntaRepositoryPort).obtenerTodas();
    }
    
    @Test
    @DisplayName("Debe obtener preguntas por temática exitosamente")
    void debeObtenerPreguntasPorTematicaExitosamente() {
        // Given
        String nombreTematica = "Arrays";
        String nombreNormalizado = "arrays";
        List<Pregunta> preguntasEsperadas = List.of(crearPreguntaEjemplo());
        
        when(preguntaRepositoryPort.buscarPorTematica(nombreNormalizado))
            .thenReturn(preguntasEsperadas);
        
        // When
        List<Pregunta> resultado = preguntaService.obtenerPreguntasPorTematica(nombreTematica);
        
        // Then
        assertEquals(1, resultado.size());
        verify(preguntaRepositoryPort).buscarPorTematica(nombreNormalizado);
    }
    
    @Test
    @DisplayName("Debe obtener todas las temáticas exitosamente")
    void debeObtenerTodasLasTematicasExitosamente() {
        // Given
        List<Tematica> tematicasEsperadas = List.of(
            new Tematica("arrays", 3, LocalDateTime.now()),
            new Tematica("bucles", 2, LocalDateTime.now())
        );
        
        when(tematicaRepositoryPort.obtenerTodas())
            .thenReturn(tematicasEsperadas);
        
        // When
        List<Tematica> resultado = preguntaService.obtenerTodasLasTematicas();
        
        // Then
        assertEquals(2, resultado.size());
        verify(tematicaRepositoryPort).obtenerTodas();
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

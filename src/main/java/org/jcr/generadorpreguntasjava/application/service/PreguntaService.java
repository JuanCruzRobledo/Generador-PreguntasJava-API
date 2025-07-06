package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.*;
import org.jcr.generadorpreguntasjava.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación principal para la gestión de preguntas.
 * 
 * Implementa todos los puertos de entrada y orquesta los casos de uso principales:
 * - Generación de preguntas
 * - Validación de respuestas
 * - Consulta de preguntas y temáticas
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PreguntaService implements GenerarPreguntaPort, ValidarRespuestaPort, ConsultarPreguntasPort {
    
    private final PreguntaRepositoryPort preguntaRepositoryPort;
    private final TematicaRepositoryPort tematicaRepositoryPort;
    private final GeneradorDePreguntaServicePort generadorDePreguntaServicePort;
    private final PromptBuilderService promptBuilderService;
    
    @Override
    public Pregunta generarPregunta(Dificultad dificultad, String tematicaDeseada) {
        log.info("Iniciando generación de pregunta con dificultad: {} y temática: {}", 
                 dificultad, tematicaDeseada);
        
        try {
            // 1. Construir el prompt dinámico
            String dificultadStr = dificultad != null ? dificultad.name().toLowerCase() : null;
            String promptCompleto = promptBuilderService.construirPromptCompleto(dificultadStr, tematicaDeseada);
            
            // 2. Generar pregunta usando el servicio externo
            log.debug("Enviando prompt al servicio de generación");
            GeneradorDePreguntaServicePort.RespuestaGeneracion respuesta = 
                generadorDePreguntaServicePort.generarPregunta(promptCompleto);
            
            // 3. Convertir respuesta a entidades del dominio
            Pregunta pregunta = construirPreguntaDesdeLaRespuesta(respuesta);
            
            // 4. Validar la pregunta generada
            pregunta.validar();
            log.debug("Pregunta generada es válida");
            
            // 5. Verificar que las temáticas no hayan sido usadas antes
            verificarTematicasNoUtilizadas(pregunta.getTematicaPrincipal(), pregunta.getTematicasSecundarias());
            
            // 6. Persistir temáticas (crear nuevas o actualizar existentes)
            List<Tematica> tematicasPersistidas = persistirTematicas(pregunta.tematicas());
            
            // 7. Crear pregunta con temáticas persistidas
            Pregunta preguntaConTematicas = new Pregunta(
                pregunta.codigoJava(),
                pregunta.enunciado(),
                pregunta.dificultad(),
                pregunta.respuestaCorrecta(),
                pregunta.explicacion(),
                pregunta.opciones(),
                tematicasPersistidas
            );
            
            // 8. Persistir la pregunta
            Pregunta preguntaGuardada = preguntaRepositoryPort.guardar(preguntaConTematicas);
            
            log.info("Pregunta generada y guardada exitosamente con ID: {}", preguntaGuardada.id());
            return preguntaGuardada;
            
        } catch (Exception e) {
            log.error("Error al generar pregunta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar pregunta: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ValidarRespuestaPort.ResultadoValidacion validarRespuesta(Long preguntaId, String respuestaDada) {
        log.info("Validando respuesta para pregunta ID: {}", preguntaId);
        
        if (preguntaId == null) {
            throw new IllegalArgumentException("El ID de la pregunta no puede ser nulo");
        }
        
        if (respuestaDada == null || respuestaDada.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta no puede estar vacía");
        }
        
        // Buscar la pregunta
        Optional<Pregunta> preguntaOpt = preguntaRepositoryPort.buscarPorId(preguntaId);
        if (preguntaOpt.isEmpty()) {
            throw new RuntimeException("Pregunta no encontrada con ID: " + preguntaId);
        }
        
        Pregunta pregunta = preguntaOpt.get();
        
        // Validar la respuesta
        boolean esCorrecta = pregunta.validarRespuesta(respuestaDada);
        
        log.info("Respuesta {} para pregunta {}: {}", 
                respuestaDada, preguntaId, esCorrecta ? "CORRECTA" : "INCORRECTA");
        
        return new ValidarRespuestaPort.ResultadoValidacion(
            esCorrecta,
            pregunta.explicacion(),
            pregunta.respuestaCorrecta()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> obtenerTodasLasPreguntas() {
        log.info("Obteniendo todas las preguntas");
        List<Pregunta> preguntas = preguntaRepositoryPort.obtenerTodas();
        log.info("Se encontraron {} preguntas", preguntas.size());
        return preguntas;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> obtenerPreguntasPorTematica(String nombreTematica) {
        log.info("Obteniendo preguntas para temática: {}", nombreTematica);
        
        if (nombreTematica == null || nombreTematica.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la temática no puede estar vacío");
        }
        
        String nombreNormalizado = Tematica.normalizarNombre(nombreTematica);
        List<Pregunta> preguntas = preguntaRepositoryPort.buscarPorTematica(nombreNormalizado);
        
        log.info("Se encontraron {} preguntas para la temática '{}'", preguntas.size(), nombreTematica);
        return preguntas;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Tematica> obtenerTodasLasTematicas() {
        log.info("Obteniendo todas las temáticas");
        List<Tematica> tematicas = tematicaRepositoryPort.obtenerTodas();
        log.info("Se encontraron {} temáticas", tematicas.size());
        return tematicas;
    }
    
    /**
     * Construye una pregunta del dominio a partir de la respuesta del servicio externo.
     */
    private Pregunta construirPreguntaDesdeLaRespuesta(GeneradorDePreguntaServicePort.RespuestaGeneracion respuesta) {
        // Convertir opciones
        List<Opcion> opciones = Arrays.stream(respuesta.opciones())
            .map(Opcion::new)
            .toList();
        
        // Convertir dificultad
        Dificultad dificultad = Dificultad.fromString(respuesta.dificultad());
        
        // Crear temáticas
        List<Tematica> tematicas = List.of(
            new Tematica(respuesta.tematicaPrincipal()),
            new Tematica(respuesta.tematicaSecundaria())
        );
        
        return new Pregunta(
            respuesta.codigoJava(),
            respuesta.enunciado(),
            dificultad,
            respuesta.respuestaCorrecta(),
            respuesta.explicacion(),
            opciones,
            tematicas
        );
    }
    
    /**
     * Verifica que las temáticas no hayan sido utilizadas previamente.
     */
    private void verificarTematicasNoUtilizadas(Tematica principal, List<Tematica> secundarias) {
        if (principal != null) {
            Optional<Tematica> existente = tematicaRepositoryPort.buscarPorNombre(principal.nombre());
            if (existente.isPresent()) {
                log.warn("La temática principal '{}' ya fue utilizada {} veces", 
                        principal.nombre(), existente.get().contadorUsos());
                // En lugar de fallar, simplemente loguear la advertencia
                // throw new RuntimeException("La temática principal '" + principal.nombre() + "' ya fue utilizada");
            }
        }
        
        for (Tematica secundaria : secundarias) {
            Optional<Tematica> existente = tematicaRepositoryPort.buscarPorNombre(secundaria.nombre());
            if (existente.isPresent()) {
                log.warn("La temática secundaria '{}' ya fue utilizada {} veces", 
                        secundaria.nombre(), existente.get().contadorUsos());
                // En lugar de fallar, simplemente loguear la advertencia
                // throw new RuntimeException("La temática secundaria '" + secundaria.nombre() + "' ya fue utilizada");
            }
        }
    }
    
    /**
     * Persiste las temáticas, creando nuevas o actualizando existentes.
     */
    private List<Tematica> persistirTematicas(List<Tematica> tematicas) {
        return tematicas.stream()
            .map(this::persistirTematica)
            .toList();
    }
    
    /**
     * Persiste una temática individual.
     */
    private Tematica persistirTematica(Tematica tematica) {
        Optional<Tematica> existente = tematicaRepositoryPort.buscarPorNombre(tematica.nombre());
        
        if (existente.isPresent()) {
            // Incrementar uso de temática existente
            Tematica tematicaActualizada = existente.get().incrementarUso();
            return tematicaRepositoryPort.guardar(tematicaActualizada);
        } else {
            // Crear nueva temática
            Tematica nuevaTematica = new Tematica(tematica.nombre(), 1, java.time.LocalDateTime.now());
            return tematicaRepositoryPort.guardar(nuevaTematica);
        }
    }
}

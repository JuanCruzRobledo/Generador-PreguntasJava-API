package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.*;
import org.jcr.generadorpreguntasjava.port.out.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Pregunta generarPregunta(Dificultad dificultad, List<String> tematicasDeseadas, List<String> tematicasYaUtilizadas) {
        log.info("Iniciando generación de pregunta con dificultad: {} y temáticas deseadas: {}", dificultad, tematicasDeseadas);

        int maxIntentos = 3; // Cantidad máxima de intentos para generar una pregunta válida

        for (int intento = 1; intento <= maxIntentos; intento++) {
            try {
                // 1. Convertir dificultad a string en minúscula para el prompt
                String dificultadStr = (dificultad != null) ? dificultad.name().toLowerCase() : null;

                // 2. Construir el prompt con dificultad, temáticas deseadas y temáticas ya utilizadas
                String promptCompleto = promptBuilderService.construirPromptCompleto(
                        dificultadStr,
                        tematicasDeseadas,
                        tematicasYaUtilizadas
                );

                log.debug("Intento {}: Enviando prompt al servicio de generación", intento);

                // 3. Enviar el prompt al servicio generador de preguntas (ej: IA externa)
                GeneradorDePreguntaServicePort.RespuestaGeneracion respuesta =
                        generadorDePreguntaServicePort.generarPregunta(promptCompleto);

                // 4. Mapear la respuesta a una entidad del dominio (Pregunta)
                Pregunta pregunta = construirPreguntaDesdeLaRespuesta(respuesta);

                // 5. Validar la estructura de la pregunta generada (opciones, campos obligatorios, etc.)
                // Aquí puede lanzar IllegalArgumentException si no es válida
                pregunta.validar();
                log.debug("Pregunta generada es válida");

                // 6. Persistir las temáticas (crear nuevas o actualizar existentes si ya existen)
                List<Tematica> tematicasPersistidas = persistirTematicas(pregunta.tematicas());

                // 7. Crear una nueva instancia de Pregunta con las temáticas persistidas
                Pregunta preguntaConTematicas = new Pregunta(
                        pregunta.codigoJava(),
                        pregunta.enunciado(),
                        pregunta.dificultad(),
                        pregunta.respuestaCorrecta(),
                        pregunta.explicacion(),
                        pregunta.opciones(),
                        tematicasPersistidas
                );

                // 8. Persistir la nueva pregunta con las temáticas correctas asociadas
                Pregunta preguntaGuardada = preguntaRepositoryPort.guardar(preguntaConTematicas);

                log.info("Pregunta generada y guardada exitosamente con ID: {}", preguntaGuardada.id());
                return preguntaGuardada;

            } catch (IllegalArgumentException e) {
                // Si la validación falla, intentamos de nuevo hasta maxIntentos
                log.warn("Intento {} falló validación: {}", intento, e.getMessage());

                if (intento == maxIntentos) {
                    // Si ya agotamos los intentos, lanzamos la excepción para que el front la reciba
                    log.error("Se agotaron los intentos de generación sin éxito");
                    throw new RuntimeException("No se pudo generar una pregunta válida tras " + maxIntentos + " intentos", e);
                }
                // Si no es el último intento, seguimos intentando sin lanzar error
            } catch (Exception e) {
                // Cualquier otro error (no de validación) se propaga inmediatamente
                log.error("Error al generar pregunta: {}", e.getMessage(), e);
                throw new RuntimeException("Error al generar pregunta: " + e.getMessage(), e);
            }
        }

        // Esto nunca debería alcanzarse, pero queda como fallback
        throw new RuntimeException("Error inesperado en la generación de pregunta");
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
     * Verifica que las temáticas no hayan sido utilizadas previamente en esta sesion de preguntas.
     */
    private void verificarTematicasNoUtilizadas(
            Tematica principal,
            List<Tematica> secundarias,
            List<String> tematicasYaUtilizadas
    ) {
        // Verifica solo si la temática principal está en la lista de ya utilizadas
        if (principal != null && tematicasYaUtilizadas.contains(principal.nombre())) {
            log.warn("La temática principal '{}' ya fue utilizada previamente en esta sesión", principal.nombre());
        }

        for (Tematica secundaria : secundarias) {
            if (tematicasYaUtilizadas.contains(secundaria.nombre())) {
                log.warn("La temática secundaria '{}' ya fue utilizada previamente en esta sesión", secundaria.nombre());
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
    @Transactional
    public Tematica persistirTematica(Tematica tematica) {
        String nombreNormalizado = tematica.nombre().toLowerCase();

        try {
            Optional<Tematica> existente = tematicaRepositoryPort.buscarPorNombre(nombreNormalizado);
            if (existente.isPresent()) {
                return tematicaRepositoryPort.guardar(incrementarContador(existente.get()));
            }

            return tematicaRepositoryPort.guardar(new Tematica(
                    nombreNormalizado,
                    1,
                    LocalDateTime.now()
            ));
        } catch (DataIntegrityViolationException e) {
            // Ocurre si otro thread insertó al mismo tiempo
            log.warn("Temática '{}' fue insertada en paralelo, recuperando de DB...", nombreNormalizado);
            return tematicaRepositoryPort.buscarPorNombre(nombreNormalizado)
                    .orElseThrow(() -> new IllegalStateException("La temática fue insertada pero no puede recuperarse", e));
        }
    }

    /**
     * Incrementar contador de Tematica.
     */
    private Tematica incrementarContador(Tematica existente) {
        return new Tematica(
                existente.id(),
                existente.nombre(),
                existente.contadorUsos() + 1,
                LocalDateTime.now()
        );
    }
}

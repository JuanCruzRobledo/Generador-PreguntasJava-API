package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.*;
import org.jcr.generadorpreguntasjava.port.out.*;
import org.springframework.core.env.Environment;
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
    private final GeneradorDePreguntaServicePort generadorDePreguntaServicePort;
    private final PromptBuilderService promptBuilderService;
    private final TematicaRepositoryPort tematicaRepositoryPort;
    private final Environment environment;

    @Override
    public Pregunta generarPregunta(Dificultad dificultad, String lenguaje,String categoriaPrincipal, List<String> tagsTematicas, List<String> tagsYaUtilizadas) {
        log.info("Iniciando generación de pregunta con dificultad: {} y temáticas deseadas: {}", dificultad, tagsTematicas);

        int maxIntentos = 3; // Cantidad máxima de intentos para generar una pregunta válida

        for (int intento = 1; intento <= maxIntentos; intento++) {
            try {
                // 1. Convertir dificultad a string en minúscula para el prompt
                String dificultadStr = (dificultad != null) ? dificultad.name().toLowerCase() : null;

                // 2. Construir el prompt con dificultad, temáticas deseadas y temáticas ya utilizadas
                String promptCompleto = promptBuilderService.construirPromptCompleto(
                        dificultadStr,
                        lenguaje,
                        categoriaPrincipal,
                        tagsTematicas,
                        tagsYaUtilizadas
                );

                log.debug("Intento {}: Enviando prompt al servicio de generación", intento);

                // 3. Enviar el prompt al servicio generador de preguntas (ej: IA externa)
                GeneradorDePreguntaServicePort.RespuestaGeneracion respuesta;

                if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
                    log.info("Usando respuesta simulada de Gemini (perfil test)");
                    respuesta = generadorDePreguntaServicePort.simularRespuesta();
                } else {
                    respuesta = generadorDePreguntaServicePort.generarPregunta(promptCompleto);
                }

                // 4. Mapear la respuesta a una entidad del dominio (Pregunta)
                Pregunta pregunta = Pregunta.fromRespuestaGeneracion(respuesta);

                // 5. Validar la estructura de la pregunta generada (opciones, campos obligatorios, etc.)
                // Aquí puede lanzar IllegalArgumentException si no es válida
                pregunta.validar();
                log.debug("Pregunta generada es válida");

                // 6. Persistir las temáticas (crear nuevas o actualizar existentes si ya existen)
                List<TagTematica> tematicasPersistidas = persistirTematicas(pregunta.tagsTematicas());

                // 7. Crear una nueva instancia de Pregunta con las temáticas persistidas
                Pregunta preguntaConTematicas = new Pregunta(
                        pregunta.codigoFuente(),
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
        
        String nombreNormalizado = TagTematica.normalizarNombre(nombreTematica);
        List<Pregunta> preguntas = preguntaRepositoryPort.buscarPorTematica(nombreNormalizado);
        
        log.info("Se encontraron {} preguntas para la temática '{}'", preguntas.size(), nombreTematica);
        return preguntas;
    }


    /**
     * Persiste las temáticas, creando nuevas o actualizando existentes.
     */
    private List<TagTematica> persistirTematicas(List<TagTematica> tagTematicas) {
        return tagTematicas.stream()
                .map(tematicaRepositoryPort::persistirConIntegridad)
                .toList();
    }
}

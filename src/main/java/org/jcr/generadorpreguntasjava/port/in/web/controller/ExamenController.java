package org.jcr.generadorpreguntasjava.port.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.ExamenMapper;
import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.Examen;
import org.jcr.generadorpreguntasjava.port.in.ConsultarExamenesPort;
import org.jcr.generadorpreguntasjava.port.in.GenerarExamenPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.request.GenerarExamenRequest;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.ExamenResponse;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de exámenes.
 *
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExamenController {

    private final GenerarExamenPort generarExamenPort;
    private final ConsultarExamenesPort consultarExamenesPort;
    private final ExamenMapper examenMapper;

    /**
     * Genera un nuevo examen con múltiples preguntas.
     *
     * POST /api/v1/examenes/generar
     */
    @PostMapping("/examenes/generar")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ExamenResponse> generarExamen(@RequestBody GenerarExamenRequest request) {
        log.info("Solicitud de generación de examen recibida. Parámetros: {}", request);

        try {
            // Validación básica de parámetros
            if (request.cantidadPreguntas() <= 0) {
                throw new IllegalArgumentException("La cantidad de preguntas debe ser mayor a cero");
            }

            // Mapear request a parámetros del dominio
            var tematicasDeseadas = request.tematicasDeseadas() != null ? request.tematicasDeseadas() : List.<String>of();
            var tematicasExcluidas = request.tematicasExcluidas() != null ? request.tematicasExcluidas() : List.<String>of();

            // Generar examen
            Examen examenGenerado = generarExamenPort.generarExamen(
                    request.titulo(),
                    request.descripcion(),
                    Dificultad.valueOf(request.dificultad().name()),
                    request.cantidadPreguntas(),
                    tematicasDeseadas,
                    tematicasExcluidas
            );

            // Mapear a DTO de respuesta
            ExamenResponse response = examenMapper.toResponse(examenGenerado);

            log.info("Examen generado exitosamente con ID: {}. Contiene {} preguntas",
                    examenGenerado.id(), examenGenerado.preguntas().size());

            return ApiResponse.exito(response, "Examen generado exitosamente");

        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos para generar examen: {}", e.getMessage());
            return ApiResponse.error("Parámetros inválidos: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error durante la generación del examen: {}", e.getMessage());
            return ApiResponse.error("Error al generar examen: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error interno al generar examen: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al generar examen", e.getMessage());
        }
    }

    /**
     * Obtiene todos los exámenes disponibles.
     *
     * GET /api/v1/examenes
     */
    @GetMapping("/examenes")
    public ApiResponse<List<ExamenResponse>> obtenerTodosLosExamenes() {
        log.info("Solicitud para obtener todos los exámenes");

        try {
            List<Examen> examenes = consultarExamenesPort.obtenerTodosLosExamenes();
            List<ExamenResponse> response = examenMapper.toResponseList(examenes);

            log.info("Se retornaron {} exámenes", examenes.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d exámenes", examenes.size()));

        } catch (Exception e) {
            log.error("Error al obtener exámenes: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener exámenes", e.getMessage());
        }
    }

    /**
     * Obtiene un examen por su ID.
     *
     * GET /api/v1/examenes/{id}
     */
    @GetMapping("/examenes/{id}")
    public ApiResponse<ExamenResponse> obtenerExamenPorId(@PathVariable Long id) {
        log.info("Solicitud para obtener examen con ID: {}", id);

        try {
            Examen examen = consultarExamenesPort.obtenerExamenPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Examen no encontrado"));

            ExamenResponse response = examenMapper.toResponse(examen);

            log.info("Examen con ID {} encontrado. Contiene {} preguntas", id, examen.preguntas().size());
            return ApiResponse.exito(response, "Examen encontrado");

        } catch (IllegalArgumentException e) {
            log.warn("Examen no encontrado: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener examen: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener examen", e.getMessage());
        }
    }

    /**
     * Obtiene exámenes filtrados por temática.
     *
     * GET /api/v1/examenes/por-tematica/{nombre}
     */
    @GetMapping("/examenes/por-tematica/{nombre}")
    public ApiResponse<List<ExamenResponse>> obtenerExamenesPorTematica(@PathVariable String nombre) {
        log.info("Solicitud para obtener exámenes por temática: {}", nombre);

        try {
            List<Examen> examenes = consultarExamenesPort.obtenerExamenesPorTematica(nombre);
            List<ExamenResponse> response = examenMapper.toResponseList(examenes);

            log.info("Se retornaron {} exámenes para la temática '{}'", examenes.size(), nombre);
            return ApiResponse.exito(response,
                    String.format("Se encontraron %d exámenes para la temática '%s'", examenes.size(), nombre));

        } catch (IllegalArgumentException e) {
            log.warn("Temática inválida: {}", e.getMessage());
            return ApiResponse.error("Temática inválida: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener exámenes por temática: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener exámenes por temática", e.getMessage());
        }
    }
}

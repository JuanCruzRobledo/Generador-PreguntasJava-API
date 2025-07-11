package org.jcr.generadorpreguntasjava.port.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.PreguntaMapper;
import org.jcr.generadorpreguntasjava.domain.model.Pregunta;
import org.jcr.generadorpreguntasjava.port.in.*;
import org.jcr.generadorpreguntasjava.port.in.web.dto.request.*;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.*;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de preguntas.
 * 
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PreguntaController {
    
    private final GenerarPreguntaPort generarPreguntaPort;
    private final ValidarRespuestaPort validarRespuestaPort;
    private final ConsultarPreguntasPort consultarPreguntasPort;
    private final PreguntaMapper preguntaMapper;
    
    /**
     * Genera una nueva pregunta de opción múltiple.
     * 
     * POST /api/v1/preguntas/generar
     */
    @PostMapping("/preguntas/generar")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PreguntaResponse> generarPregunta(@RequestBody(required = false) GenerarPreguntaRequest request) {
        log.info("Solicitud de generación de pregunta recibida: {}", request);
        
        try {
            // Mapear request a parámetros del dominio
            var dificultad = request != null ? preguntaMapper.mapDificultad(request) : null;

            String categoriaPrincipal = request != null ? request.categoriaPrincipal() : null;
            String lenguajeProgramacion = request != null ? request.lenguaje() : null;
            List<String> tematicasDeseadas = request != null ? request.tagsTematicas() : List.of();
            List<String> tematicasYaUtilizadas = request != null ? request.tagsYaUtilizados() : List.of();
            
            // Generar pregunta
            Pregunta preguntaGenerada = generarPreguntaPort.generarPregunta(dificultad,lenguajeProgramacion,categoriaPrincipal, tematicasDeseadas, tematicasYaUtilizadas);
            
            // Mapear a DTO de respuesta
            PreguntaResponse response = preguntaMapper.toResponse(preguntaGenerada);
            
            log.info("Pregunta generada exitosamente con ID: {}", preguntaGenerada.id());
            return ApiResponse.exito(response, "Pregunta generada exitosamente");
            
        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos para generar pregunta: {}", e.getMessage());
            return ApiResponse.error("Parámetros inválidos: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al generar pregunta: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al generar pregunta", e.getMessage());
        }
    }
    
    /**
     * Valida una respuesta proporcionada por el usuario.
     * 
     * POST /api/v1/respuesta
     */
    @PostMapping("/respuesta")
    public ApiResponse<ValidacionResponse> validarRespuesta(@RequestBody ValidarRespuestaRequest request) {
        log.info("Solicitud de validación de respuesta recibida para pregunta ID: {}", request.preguntaId());
        
        try {
            // Validar respuesta
            ValidarRespuestaPort.ResultadoValidacion resultado = 
                validarRespuestaPort.validarRespuesta(request.preguntaId(), request.opcionSeleccionada());
            
            // Mapear a DTO de respuesta
            ValidacionResponse response = preguntaMapper.toResponse(resultado);
            
            String mensaje = resultado.esCorrecta() ? 
                "¡Respuesta correcta!" : 
                "Respuesta incorrecta";
                
            log.info("Validación completada para pregunta {}: {}", 
                    request.preguntaId(), resultado.esCorrecta() ? "CORRECTA" : "INCORRECTA");
            
            return ApiResponse.exito(response, mensaje);
            
        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos para validar respuesta: {}", e.getMessage());
            return ApiResponse.error("Parámetros inválidos: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error al validar respuesta: {}", e.getMessage());
            return ApiResponse.error("Error al validar respuesta: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error interno al validar respuesta: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al validar respuesta", e.getMessage());
        }
    }
    
    /**
     * Obtiene todas las preguntas disponibles.
     * 
     * GET /api/v1/preguntas
     */
    @GetMapping("/preguntas")
    public ApiResponse<List<PreguntaResponse>> obtenerTodasLasPreguntas() {
        log.info("Solicitud para obtener todas las preguntas");
        
        try {
            List<Pregunta> preguntas = consultarPreguntasPort.obtenerTodasLasPreguntas();
            List<PreguntaResponse> response = preguntaMapper.toResponseList(preguntas);
            
            log.info("Se retornaron {} preguntas", preguntas.size());
            return ApiResponse.exito(response, String.format("Se encontraron %d preguntas", preguntas.size()));
            
        } catch (Exception e) {
            log.error("Error al obtener preguntas: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener preguntas", e.getMessage());
        }
    }
    
    /**
     * Obtiene preguntas filtradas por temática.
     * 
     * GET /api/v1/preguntas/por-tematica/{nombre}
     */
    @GetMapping("/preguntas/por-tematica/{nombre}")
    public ApiResponse<List<PreguntaResponse>> obtenerPreguntasPorTematica(@PathVariable String nombre) {
        log.info("Solicitud para obtener preguntas por temática: {}", nombre);
        
        try {
            List<Pregunta> preguntas = consultarPreguntasPort.obtenerPreguntasPorTematica(nombre);
            List<PreguntaResponse> response = preguntaMapper.toResponseList(preguntas);
            
            log.info("Se retornaron {} preguntas para la temática '{}'", preguntas.size(), nombre);
            return ApiResponse.exito(response, 
                String.format("Se encontraron %d preguntas para la temática '%s'", preguntas.size(), nombre));
            
        } catch (IllegalArgumentException e) {
            log.warn("Temática inválida: {}", e.getMessage());
            return ApiResponse.error("Temática inválida: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener preguntas por temática: {}", e.getMessage(), e);
            return ApiResponse.error("Error interno al obtener preguntas por temática", e.getMessage());
        }
    }
}

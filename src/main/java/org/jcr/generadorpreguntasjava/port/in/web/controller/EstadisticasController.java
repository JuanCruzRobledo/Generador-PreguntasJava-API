package org.jcr.generadorpreguntasjava.port.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.mapper.SesionRespuestaMapper;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasPorDificultad;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasPorTematica;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasUsuario;
import org.jcr.generadorpreguntasjava.port.in.ConsultarEstadisticasPort;
import org.jcr.generadorpreguntasjava.port.in.RegistrarRespuestaPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.SesionRespuestaResponse;
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de estadísticas y sesiones de respuesta.
 *
 * Adaptador primario que traduce peticiones HTTP en llamadas a los puertos de entrada
 * para operaciones relacionadas con el seguimiento de respuestas y estadísticas de usuarios.
 */
@Slf4j
@RestController
@RequestMapping("/v1/estadisticas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EstadisticasController {

    private final RegistrarRespuestaPort registrarRespuestaPort;
    private final ConsultarEstadisticasPort consultarEstadisticasPort;
    private final SesionRespuestaMapper sesionMapper;

    /**
     * Inicia una nueva sesión de respuesta para un usuario y una pregunta.
     *
     * POST /api/v1/estadisticas/sesiones/iniciar
     */
    @PostMapping("/sesiones/iniciar")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SesionRespuestaResponse> iniciarSesion(
            @RequestParam Long usuarioId,
            @RequestParam Long preguntaId) {

        log.info("Iniciando sesión para usuario {} y pregunta {}", usuarioId, preguntaId);
        try {
            var sesion = registrarRespuestaPort.iniciarRespuesta(usuarioId, preguntaId);
            return ApiResponse.exito(sesionMapper.toResponse(sesion),
                    "Sesión iniciada exitosamente");
        } catch (Exception e) {
            log.error("Error al iniciar sesión: {}", e.getMessage(), e);
            return ApiResponse.error("No se pudo iniciar sesión", e.getMessage());
        }
    }

    /**
     * Registra la respuesta y finaliza la sesión.
     *
     * POST /api/v1/estadisticas/responder
     */
    @PostMapping("/responder")
    public ApiResponse<RegistrarRespuestaPort.ResultadoRespuesta> responderPregunta(
            @RequestParam Long usuarioId,
            @RequestParam Long preguntaId,
            @RequestParam String respuesta) {

        log.info("Respondiendo pregunta {} del usuario {} con opción {}", preguntaId, usuarioId, respuesta);
        try {
            var resultado = registrarRespuestaPort.responderPregunta(usuarioId, preguntaId, respuesta);
            return ApiResponse.exito(resultado, "Respuesta registrada correctamente");
        } catch (Exception e) {
            log.error("Error al registrar respuesta: {}", e.getMessage(), e);
            return ApiResponse.error("No se pudo registrar la respuesta", e.getMessage());
        }
    }

    /**
     * Obtiene las estadísticas generales del usuario.
     *
     * GET /api/v1/estadisticas/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ApiResponse<EstadisticasUsuario> obtenerEstadisticas(@PathVariable Long usuarioId) {
        try {
            var stats = consultarEstadisticasPort.obtenerEstadisticas(usuarioId);
            return ApiResponse.exito(stats, "Estadísticas obtenidas");
        } catch (Exception e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage(), e);
            return ApiResponse.error("Error al obtener estadísticas", e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas por dificultad.
     *
     * GET /api/v1/estadisticas/usuario/{usuarioId}/dificultades
     */
    @GetMapping("/usuario/{usuarioId}/dificultades")
    public ApiResponse<List<EstadisticasPorDificultad>> obtenerEstadisticasPorDificultad(@PathVariable Long usuarioId) {
        try {
            var lista = consultarEstadisticasPort.obtenerEstadisticasPorDificultad(usuarioId);
            return ApiResponse.exito(lista, "Estadísticas por dificultad obtenidas");
        } catch (Exception e) {
            log.error("Error al obtener estadísticas por dificultad: {}", e.getMessage(), e);
            return ApiResponse.error("Error al obtener estadísticas por dificultad", e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas por temática.
     *
     * GET /api/v1/estadisticas/usuario/{usuarioId}/tematicas
     */
    @GetMapping("/usuario/{usuarioId}/tematicas")
    public ApiResponse<List<EstadisticasPorTematica>> obtenerEstadisticasPorTematica(@PathVariable Long usuarioId) {
        try {
            var lista = consultarEstadisticasPort.obtenerEstadisticasPorTematica(usuarioId);
            return ApiResponse.exito(lista, "Estadísticas por temática obtenidas");
        } catch (Exception e) {
            log.error("Error al obtener estadísticas por temática: {}", e.getMessage(), e);
            return ApiResponse.error("Error al obtener estadísticas por temática", e.getMessage());
        }
    }

    /**
     * Obtiene un resumen del progreso del usuario.
     *
     * GET /api/v1/estadisticas/usuario/{usuarioId}/resumen
     */
    @GetMapping("/usuario/{usuarioId}/resumen")
    public ApiResponse<ConsultarEstadisticasPort.ResumenProgreso> obtenerResumenProgreso(@PathVariable Long usuarioId) {
        try {
            var resumen = consultarEstadisticasPort.obtenerResumenProgreso(usuarioId);
            return ApiResponse.exito(resumen, "Resumen de progreso obtenido");
        } catch (Exception e) {
            log.error("Error al obtener resumen de progreso: {}", e.getMessage(), e);
            return ApiResponse.error("Error al obtener resumen", e.getMessage());
        }
    }

    /**
     * Obtiene el ranking global.
     *
     * GET /api/v1/estadisticas/ranking?limite=10
     */
    @GetMapping("/ranking")
    public ApiResponse<ConsultarEstadisticasPort.RankingGlobal> obtenerRankingGlobal(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            var ranking = consultarEstadisticasPort.obtenerRankingsGlobales(limite);
            return ApiResponse.exito(ranking, "Ranking global obtenido");
        } catch (Exception e) {
            log.error("Error al obtener ranking global: {}", e.getMessage(), e);
            return ApiResponse.error("Error al obtener ranking global", e.getMessage());
        }
    }
}
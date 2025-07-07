package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.SesionRespuesta;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para casos de uso de registro de respuestas con seguimiento de tiempo.
 * Define las operaciones para manejar sesiones de respuesta y timing.
 */
public interface RegistrarRespuestaPort {
    
    /**
     * Inicia una nueva sesión de respuesta para un usuario y pregunta.
     * 
     * @param usuarioId ID del usuario
     * @param preguntaId ID de la pregunta
     * @return Sesión de respuesta iniciada
     * @throws IllegalArgumentException si los IDs son null
     * @throws RuntimeException si ya existe una sesión en progreso o si el usuario/pregunta no existen
     */
    SesionRespuesta iniciarRespuesta(Long usuarioId, Long preguntaId);
    
    /**
     * Completa una sesión de respuesta con la respuesta del usuario.
     * 
     * @param sesionId ID de la sesión a completar
     * @param respuesta Respuesta seleccionada por el usuario
     * @return Sesión completada con resultado y tiempo
     * @throws IllegalArgumentException si los parámetros son null o vacíos
     * @throws RuntimeException si la sesión no existe o ya está completada
     */
    SesionRespuesta completarRespuesta(Long sesionId, String respuesta);
    
    /**
     * Busca una sesión en progreso para un usuario y pregunta específicos.
     * 
     * @param usuarioId ID del usuario
     * @param preguntaId ID de la pregunta
     * @return Optional con la sesión si existe
     */
    Optional<SesionRespuesta> buscarSesionEnProgreso(Long usuarioId, Long preguntaId);
    
    /**
     * Obtiene todas las sesiones de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de sesiones del usuario
     * @throws IllegalArgumentException si el usuarioId es null
     */
    List<SesionRespuesta> obtenerSesionesPorUsuario(Long usuarioId);
    
    /**
     * Obtiene las sesiones completadas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de sesiones completadas
     * @throws IllegalArgumentException si el usuarioId es null
     */
    List<SesionRespuesta> obtenerSesionesCompletadas(Long usuarioId);
    
    /**
     * Obtiene las últimas N sesiones de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @param limite Número máximo de sesiones a obtener
     * @return Lista de las últimas sesiones
     * @throws IllegalArgumentException si el usuarioId es null o limite <= 0
     */
    List<SesionRespuesta> obtenerUltimasSesiones(Long usuarioId, int limite);
    
    /**
     * Cancela sesiones abandonadas (más de 30 minutos sin completar).
     * 
     * @return Número de sesiones canceladas
     */
    int cancelarSesionesAbandonadas();
    
    /**
     * Verifica si un usuario tiene sesiones en progreso.
     * 
     * @param usuarioId ID del usuario
     * @return true si tiene sesiones en progreso
     */
    boolean tieneSesionesEnProgreso(Long usuarioId);
    
    /**
     * Resultado de completar una respuesta, incluyendo validación y timing.
     */
    record ResultadoRespuesta(
        Long sesionId,
        Long usuarioId,
        Long preguntaId,
        String respuestaSeleccionada,
        boolean esCorrecta,
        long tiempoRespuestaMs,
        String explicacion,
        String respuestaCorrecta
    ) {}
    
    /**
     * Completa una respuesta con validación automática y retorna resultado completo.
     * Este método combina completarRespuesta con validación.
     * 
     * @param usuarioId ID del usuario
     * @param preguntaId ID de la pregunta
     * @param respuesta Respuesta seleccionada
     * @return Resultado completo de la respuesta
     * @throws IllegalArgumentException si los parámetros son null o vacíos
     * @throws RuntimeException si no hay sesión en progreso o hay errores de validación
     */
    ResultadoRespuesta responderPregunta(Long usuarioId, Long preguntaId, String respuesta);
}

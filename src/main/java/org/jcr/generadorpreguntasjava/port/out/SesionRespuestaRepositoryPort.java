package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.SesionRespuesta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de sesiones de respuesta.
 * Define el contrato para operaciones de persistencia de sesiones de respuesta.
 */
public interface SesionRespuestaRepositoryPort {
    
    /**
     * Guarda una sesión de respuesta nueva o actualiza una existente.
     * 
     * @param sesion Sesión a guardar
     * @return Sesión guardada con ID asignado
     */
    SesionRespuesta guardar(SesionRespuesta sesion);
    
    /**
     * Busca una sesión por su ID.
     * 
     * @param id ID de la sesión
     * @return Optional con la sesión si existe
     */
    Optional<SesionRespuesta> buscarPorId(Long id);
    
    /**
     * Obtiene todas las sesiones de respuesta de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de sesiones del usuario
     */
    List<SesionRespuesta> obtenerPorUsuario(Long usuarioId);
    
    /**
     * Obtiene todas las sesiones completadas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de sesiones completadas del usuario
     */
    List<SesionRespuesta> obtenerCompletadasPorUsuario(Long usuarioId);
    
    /**
     * Obtiene sesiones de respuesta de un usuario filtradas por dificultad.
     * 
     * @param usuarioId ID del usuario
     * @param dificultad Dificultad a filtrar
     * @return Lista de sesiones del usuario para esa dificultad
     */
    List<SesionRespuesta> obtenerPorUsuarioYDificultad(Long usuarioId, Dificultad dificultad);
    
    /**
     * Obtiene sesiones de respuesta de un usuario filtradas por temática.
     * 
     * @param usuarioId ID del usuario
     * @param tematica Temática a filtrar
     * @return Lista de sesiones del usuario para esa temática
     */
    List<SesionRespuesta> obtenerPorUsuarioYTematica(Long usuarioId, String tematica);
    
    /**
     * Obtiene sesiones de respuesta de un usuario en un rango de fechas.
     * 
     * @param usuarioId ID del usuario
     * @param desde Fecha desde
     * @param hasta Fecha hasta
     * @return Lista de sesiones del usuario en el rango
     */
    List<SesionRespuesta> obtenerPorUsuarioYFechas(Long usuarioId, 
                                                   LocalDateTime desde, 
                                                   LocalDateTime hasta);
    
    /**
     * Obtiene las sesiones en progreso de un usuario (no completadas).
     * 
     * @param usuarioId ID del usuario
     * @return Lista de sesiones en progreso
     */
    List<SesionRespuesta> obtenerEnProgresoPorUsuario(Long usuarioId);
    
    /**
     * Busca una sesión en progreso específica de un usuario para una pregunta.
     * 
     * @param usuarioId ID del usuario
     * @param preguntaId ID de la pregunta
     * @return Optional con la sesión si existe
     */
    Optional<SesionRespuesta> buscarSesionEnProgreso(Long usuarioId, Long preguntaId);
    
    /**
     * Cuenta el total de sesiones completadas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de sesiones completadas
     */
    long contarCompletadasPorUsuario(Long usuarioId);
    
    /**
     * Cuenta las respuestas correctas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Número de respuestas correctas
     */
    long contarCorrectasPorUsuario(Long usuarioId);
    
    /**
     * Elimina sesiones abandonadas (más de 30 minutos sin completar).
     * 
     * @return Número de sesiones eliminadas
     */
    int eliminarSesionesAbandonadas();
    
    /**
     * Obtiene las últimas N sesiones de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @param limite Número máximo de sesiones a obtener
     * @return Lista de las últimas sesiones
     */
    List<SesionRespuesta> obtenerUltimasSesiones(Long usuarioId, int limite);
    
    /**
     * Verifica si existe una sesión para un usuario y pregunta específicos.
     * 
     * @param usuarioId ID del usuario
     * @param preguntaId ID de la pregunta
     * @return true si existe la sesión
     */
    boolean existeSesion(Long usuarioId, Long preguntaId);
}

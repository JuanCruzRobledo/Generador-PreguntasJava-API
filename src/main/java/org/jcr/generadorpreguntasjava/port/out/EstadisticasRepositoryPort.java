package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.EstadisticasUsuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de estadísticas de usuario.
 * Define el contrato para operaciones de persistencia de estadísticas.
 */
public interface EstadisticasRepositoryPort {
    
    /**
     * Guarda las estadísticas de un usuario.
     * 
     * @param estadisticas Estadísticas a guardar
     * @return Estadísticas guardadas
     */
    EstadisticasUsuario guardar(EstadisticasUsuario estadisticas);
    
    /**
     * Busca las estadísticas de un usuario por ID.
     * 
     * @param usuarioId ID del usuario
     * @return Optional con las estadísticas si existen
     */
    Optional<EstadisticasUsuario> buscarPorUsuario(Long usuarioId);
    
    /**
     * Obtiene las estadísticas de todos los usuarios.
     * 
     * @return Lista de estadísticas de todos los usuarios
     */
    List<EstadisticasUsuario> obtenerTodas();
    
    /**
     * Obtiene las estadísticas de usuarios con al menos una respuesta.
     * 
     * @return Lista de estadísticas de usuarios activos
     */
    List<EstadisticasUsuario> obtenerDeUsuariosActivos();
    
    /**
     * Elimina las estadísticas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return true si se eliminaron las estadísticas
     */
    boolean eliminarPorUsuario(Long usuarioId);
    
    /**
     * Verifica si existen estadísticas para un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return true si existen estadísticas
     */
    boolean existenPorUsuario(Long usuarioId);
    
    /**
     * Cuenta el número de usuarios con estadísticas.
     * 
     * @return Número de usuarios con estadísticas
     */
    long contarUsuariosConEstadisticas();
    
    /**
     * Obtiene el ranking de usuarios por porcentaje de aciertos.
     * 
     * @param limite Número máximo de usuarios en el ranking
     * @return Lista de estadísticas ordenadas por porcentaje descendente
     */
    List<EstadisticasUsuario> obtenerRankingPorAciertos(int limite);
    
    /**
     * Obtiene el ranking de usuarios por número de preguntas respondidas.
     * 
     * @param limite Número máximo de usuarios en el ranking
     * @return Lista de estadísticas ordenadas por total de preguntas descendente
     */
    List<EstadisticasUsuario> obtenerRankingPorVolumen(int limite);
    
    /**
     * Obtiene usuarios con mejor tiempo promedio de respuesta.
     * 
     * @param limite Número máximo de usuarios
     * @return Lista de estadísticas ordenadas por tiempo promedio ascendente
     */
    List<EstadisticasUsuario> obtenerRankingPorTiempo(int limite);
}

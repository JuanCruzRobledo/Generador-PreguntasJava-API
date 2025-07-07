package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasUsuario;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasPorDificultad;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasPorTematica;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para casos de uso de consulta de estadísticas.
 * Define las operaciones para obtener y calcular estadísticas de usuarios.
 */
public interface ConsultarEstadisticasPort {
    
    /**
     * Obtiene las estadísticas completas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Estadísticas del usuario
     * @throws IllegalArgumentException si el usuarioId es null
     * @throws RuntimeException si el usuario no existe
     */
    EstadisticasUsuario obtenerEstadisticas(Long usuarioId);
    
    /**
     * Recalcula las estadísticas de un usuario desde cero basándose en sus sesiones.
     * 
     * @param usuarioId ID del usuario
     * @return Estadísticas recalculadas y actualizadas
     * @throws IllegalArgumentException si el usuarioId es null
     * @throws RuntimeException si el usuario no existe
     */
    EstadisticasUsuario recalcularEstadisticas(Long usuarioId);
    
    /**
     * Obtiene las estadísticas por dificultad de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de estadísticas por dificultad
     * @throws IllegalArgumentException si el usuarioId es null
     */
    List<EstadisticasPorDificultad> obtenerEstadisticasPorDificultad(Long usuarioId);
    
    /**
     * Obtiene las estadísticas por temática de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de estadísticas por temática
     * @throws IllegalArgumentException si el usuarioId es null
     */
    List<EstadisticasPorTematica> obtenerEstadisticasPorTematica(Long usuarioId);
    
    /**
     * Obtiene el ranking de temáticas del usuario ordenado por rendimiento.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de temáticas ordenadas por porcentaje de aciertos descendente
     * @throws IllegalArgumentException si el usuarioId es null
     */
    List<EstadisticasPorTematica> obtenerRankingTematicas(Long usuarioId);
    
    /**
     * Obtiene estadísticas específicas para una dificultad.
     * 
     * @param usuarioId ID del usuario
     * @param dificultad Dificultad específica
     * @return Estadísticas para esa dificultad o estadísticas vacías si no hay datos
     * @throws IllegalArgumentException si los parámetros son null
     */
    EstadisticasPorDificultad obtenerEstadisticasPorDificultad(Long usuarioId, Dificultad dificultad);
    
    /**
     * Obtiene estadísticas específicas para una temática.
     * 
     * @param usuarioId ID del usuario
     * @param tematica Nombre de la temática
     * @return Estadísticas para esa temática o estadísticas vacías si no hay datos
     * @throws IllegalArgumentException si los parámetros son null o vacíos
     */
    EstadisticasPorTematica obtenerEstadisticasPorTematica(Long usuarioId, String tematica);
    
    /**
     * Verifica si un usuario tiene estadísticas registradas.
     * 
     * @param usuarioId ID del usuario
     * @return true si el usuario tiene al menos una respuesta registrada
     * @throws IllegalArgumentException si el usuarioId es null
     */
    boolean tieneEstadisticas(Long usuarioId);
    
    /**
     * Obtiene un resumen de progreso del usuario.
     */
    record ResumenProgreso(
        int totalPreguntas,
        int respuestasCorrectas,
        double porcentajeAciertos,
        String tiempoPromedio,
        String nivelUsuario,  // "Principiante", "Intermedio", "Avanzado"
        String dificultadFavorita,
        String tematicaFavorita,
        boolean tieneBuenRendimiento
    ) {}
    
    /**
     * Obtiene un resumen del progreso del usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Resumen de progreso
     * @throws IllegalArgumentException si el usuarioId es null
     */
    ResumenProgreso obtenerResumenProgreso(Long usuarioId);
    
    /**
     * Estadísticas comparativas globales.
     */
    record EstadisticasComparativas(
        double porcentajePromedioGlobal,
        long tiempoPromedioGlobal,
        int posicionEnRanking,
        int totalUsuarios,
        boolean superaPromedioGlobal
    ) {}
    
    /**
     * Obtiene estadísticas comparativas del usuario contra el promedio global.
     * 
     * @param usuarioId ID del usuario
     * @return Estadísticas comparativas
     * @throws IllegalArgumentException si el usuarioId es null
     */
    EstadisticasComparativas obtenerEstadisticasComparativas(Long usuarioId);
    
    /**
     * Rankings globales.
     */
    record RankingGlobal(
        List<EstadisticasUsuario> topPorAciertos,
        List<EstadisticasUsuario> topPorVolumen,
        List<EstadisticasUsuario> topPorTiempo
    ) {}
    
    /**
     * Obtiene los rankings globales del sistema.
     * 
     * @param limite Número máximo de usuarios por ranking
     * @return Rankings globales
     * @throws IllegalArgumentException si limite <= 0
     */
    RankingGlobal obtenerRankingsGlobales(int limite);
}

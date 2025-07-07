package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.EstadisticasUsuarioEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para estadísticas de usuario.
 */
@Repository
public interface SpringDataEstadisticasRepository extends JpaRepository<EstadisticasUsuarioEntity, Long> {
    @Query("SELECT e FROM EstadisticasUsuarioEntity e ORDER BY e.porcentajeAciertos DESC")
    List<EstadisticasUsuarioEntity> findTopByOrderByPorcentajeAciertosDesc(Pageable pageable);

    @Query("SELECT e FROM EstadisticasUsuarioEntity e ORDER BY e.totalPreguntas DESC")
    List<EstadisticasUsuarioEntity> findTopByOrderByTotalPreguntasDesc(Pageable pageable);

    @Query("SELECT e FROM EstadisticasUsuarioEntity e ORDER BY e.tiempoPromedioMs ASC")
    List<EstadisticasUsuarioEntity> findTopByOrderByTiempoPromedioAsc(Pageable pageable);

    /**
     * Busca estadísticas por ID de usuario.
     */
    Optional<EstadisticasUsuarioEntity> findByUsuarioId(Long usuarioId);
    
    /**
     * Verifica si existen estadísticas para un usuario.
     */
    boolean existsByUsuarioId(Long usuarioId);
    
    /**
     * Elimina estadísticas por ID de usuario.
     */
    int deleteByUsuarioId(Long usuarioId);
    
    /**
     * Obtiene estadísticas de usuarios con al menos una pregunta respondida.
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0")
    List<EstadisticasUsuarioEntity> findDeUsuariosActivos();
    
    /**
     * Cuenta usuarios con estadísticas.
     */
    @Query("SELECT COUNT(e) FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0")
    long countUsuariosConEstadisticas();
    
    /**
     * Obtiene ranking por porcentaje de aciertos.
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0 " +
           "ORDER BY e.porcentajeAciertos DESC, e.totalPreguntas DESC")
    List<EstadisticasUsuarioEntity> findRankingPorAciertos();
    
    /**
     * Obtiene ranking por volumen de preguntas respondidas.
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0 " +
           "ORDER BY e.totalPreguntas DESC, e.porcentajeAciertos DESC")
    List<EstadisticasUsuarioEntity> findRankingPorVolumen();
    
    /**
     * Obtiene ranking por mejor tiempo promedio.
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0 " +
           "AND e.tiempoPromedioMs IS NOT NULL AND e.tiempoPromedioMs > 0 " +
           "ORDER BY e.tiempoPromedioMs ASC, e.porcentajeAciertos DESC")
    List<EstadisticasUsuarioEntity> findRankingPorTiempo();
    
    /**
     * Obtiene estadísticas por rango de porcentaje de aciertos.
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0 " +
           "AND e.porcentajeAciertos BETWEEN :min AND :max " +
           "ORDER BY e.porcentajeAciertos DESC")
    List<EstadisticasUsuarioEntity> findByRangoPorcentaje(@Param("min") double min, @Param("max") double max);
    
    /**
     * Obtiene usuarios con mejor rendimiento (>= 70% aciertos).
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas >= 10 " +
           "AND e.porcentajeAciertos >= 70.0 ORDER BY e.porcentajeAciertos DESC")
    List<EstadisticasUsuarioEntity> findUsuariosConBuenRendimiento();
    
    /**
     * Obtiene usuarios principiantes (< 10 preguntas).
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0 " +
           "AND e.totalPreguntas < 10 ORDER BY e.totalPreguntas DESC")
    List<EstadisticasUsuarioEntity> findUsuariosPrincipiantes();
    
    /**
     * Obtiene usuarios experimentados (>= 50 preguntas).
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas >= 50 " +
           "ORDER BY e.totalPreguntas DESC")
    List<EstadisticasUsuarioEntity> findUsuariosExperimentados();
    
    /**
     * Obtiene estadísticas desactualizadas (más de 1 hora).
     */
    @Query("SELECT e FROM EstadisticasUsuarioEntity e WHERE e.ultimaActualizacion < :limite")
    List<EstadisticasUsuarioEntity> findEstadisticasDesactualizadas(@Param("limite") java.time.LocalDateTime limite);
    
    /**
     * Calcula promedios globales.
     */
    @Query("SELECT AVG(e.porcentajeAciertos) FROM EstadisticasUsuarioEntity e WHERE e.totalPreguntas > 0")
    Double calcularPorcentajePromedioGlobal();
    
    @Query("SELECT AVG(e.tiempoPromedioMs) FROM EstadisticasUsuarioEntity e " +
           "WHERE e.totalPreguntas > 0 AND e.tiempoPromedioMs IS NOT NULL AND e.tiempoPromedioMs > 0")
    Double calcularTiempoPromedioGlobal();
    
    /**
     * Obtiene top N por cada categoría con limit personalizado.
     */
    @Query(value = "SELECT * FROM estadisticas_usuario e WHERE e.total_preguntas > 0 " +
           "ORDER BY e.porcentaje_aciertos DESC, e.total_preguntas DESC LIMIT :limite", 
           nativeQuery = true)
    List<EstadisticasUsuarioEntity> findTopPorAciertos(@Param("limite") int limite);
    
    @Query(value = "SELECT * FROM estadisticas_usuario e WHERE e.total_preguntas > 0 " +
           "ORDER BY e.total_preguntas DESC, e.porcentaje_aciertos DESC LIMIT :limite", 
           nativeQuery = true)
    List<EstadisticasUsuarioEntity> findTopPorVolumen(@Param("limite") int limite);
    
    @Query(value = "SELECT * FROM estadisticas_usuario e WHERE e.total_preguntas > 0 " +
           "AND e.tiempo_promedio_ms IS NOT NULL AND e.tiempo_promedio_ms > 0 " +
           "ORDER BY e.tiempo_promedio_ms ASC, e.porcentaje_aciertos DESC LIMIT :limite", 
           nativeQuery = true)
    List<EstadisticasUsuarioEntity> findTopPorTiempo(@Param("limite") int limite);
}

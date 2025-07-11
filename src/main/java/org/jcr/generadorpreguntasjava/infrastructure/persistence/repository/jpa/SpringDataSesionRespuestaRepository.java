package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.SesionRespuestaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para sesiones de respuesta.
 */
@Repository
public interface SpringDataSesionRespuestaRepository extends JpaRepository<SesionRespuestaEntity, Long> {
    
    /**
     * Obtiene todas las sesiones de un usuario.
     */
    List<SesionRespuestaEntity> findByUsuarioIdOrderByInicioRespuestaDesc(Long usuarioId);
    
    /**
     * Obtiene las sesiones completadas de un usuario.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.finRespuesta IS NOT NULL ORDER BY s.finRespuesta DESC")
    List<SesionRespuestaEntity> findCompletadasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Obtiene las sesiones en progreso de un usuario.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.finRespuesta IS NULL ORDER BY s.inicioRespuesta DESC")
    List<SesionRespuestaEntity> findEnProgresoByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Busca una sesión en progreso específica para usuario y pregunta.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.preguntaId = :preguntaId AND s.finRespuesta IS NULL")
    Optional<SesionRespuestaEntity> findSesionEnProgreso(@Param("usuarioId") Long usuarioId, 
                                                        @Param("preguntaId") Long preguntaId);
    
    /**
     * Obtiene sesiones de un usuario filtradas por dificultad.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s JOIN s.pregunta p " +
           "WHERE s.usuarioId = :usuarioId AND p.dificultad = :dificultad " +
           "AND s.finRespuesta IS NOT NULL ORDER BY s.finRespuesta DESC")
    List<SesionRespuestaEntity> findByUsuarioIdAndDificultad(@Param("usuarioId") Long usuarioId, 
                                                            @Param("dificultad") String dificultad);
    
    /**
     * Obtiene sesiones de un usuario filtradas por temática.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s JOIN s.pregunta p JOIN p.tagsTematicas t " +
           "WHERE s.usuarioId = :usuarioId AND t.nombre = :tematica " +
           "AND s.finRespuesta IS NOT NULL ORDER BY s.finRespuesta DESC")
    List<SesionRespuestaEntity> findByUsuarioIdAndTematica(@Param("usuarioId") Long usuarioId, 
                                                          @Param("tematica") String tematica);
    
    /**
     * Obtiene sesiones de un usuario en un rango de fechas.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.inicioRespuesta BETWEEN :desde AND :hasta " +
           "AND s.finRespuesta IS NOT NULL ORDER BY s.finRespuesta DESC")
    List<SesionRespuestaEntity> findByUsuarioIdAndFechas(@Param("usuarioId") Long usuarioId,
                                                        @Param("desde") LocalDateTime desde,
                                                        @Param("hasta") LocalDateTime hasta);
    
    /**
     * Obtiene las últimas N sesiones de un usuario.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.finRespuesta IS NOT NULL ORDER BY s.finRespuesta DESC")
    List<SesionRespuestaEntity> findTopByUsuarioIdOrderByFinRespuestaDesc(@Param("usuarioId") Long usuarioId);
    
    /**
     * Cuenta las sesiones completadas de un usuario.
     */
    @Query("SELECT COUNT(s) FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.finRespuesta IS NOT NULL")
    long countCompletadasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Cuenta las respuestas correctas de un usuario.
     */
    @Query("SELECT COUNT(s) FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.esCorrecta = true AND s.finRespuesta IS NOT NULL")
    long countCorrectasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Elimina sesiones abandonadas (más de 30 minutos sin completar).
     */
    @Modifying
    @Query("DELETE FROM SesionRespuestaEntity s WHERE s.finRespuesta IS NULL " +
           "AND s.inicioRespuesta < :tiempoLimite")
    int eliminarSesionesAbandonadas(@Param("tiempoLimite") LocalDateTime tiempoLimite);
    
    /**
     * Verifica si existe una sesión para usuario y pregunta específicos.
     */
    boolean existsByUsuarioIdAndPreguntaId(Long usuarioId, Long preguntaId);
    
    /**
     * Obtiene estadísticas básicas de tiempo promedio por usuario.
     */
    @Query("SELECT AVG(s.tiempoRespuestaMs) FROM SesionRespuestaEntity s " +
           "WHERE s.usuarioId = :usuarioId AND s.finRespuesta IS NOT NULL " +
           "AND s.tiempoRespuestaMs BETWEEN 5000 AND 600000") // Entre 5 segundos y 10 minutos
    Double obtenerTiempoPromedioByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Obtiene las mejores N sesiones por tiempo de respuesta de un usuario.
     */
    @Query("SELECT s FROM SesionRespuestaEntity s WHERE s.usuarioId = :usuarioId " +
           "AND s.finRespuesta IS NOT NULL AND s.esCorrecta = true " +
           "AND s.tiempoRespuestaMs BETWEEN 5000 AND 600000 " +
           "ORDER BY s.tiempoRespuestaMs ASC")
    List<SesionRespuestaEntity> findMejoresTiemposByUsuarioId(@Param("usuarioId") Long usuarioId);
}

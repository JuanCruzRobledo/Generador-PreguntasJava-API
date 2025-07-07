package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.PreguntaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para preguntas.
 */
@Repository
public interface SpringDataPreguntaRepository extends JpaRepository<PreguntaEntity, Long> {
    
    /**
     * Busca preguntas que contengan una temática específica.
     */
    @Query("SELECT DISTINCT p FROM PreguntaEntity p " +
           "JOIN p.tematicas t " +
           "WHERE t.nombre = :nombreTematica")
    List<PreguntaEntity> findByTematicasNombre(@Param("nombreTematica") String nombreTematica);
    
    /**
     * Obtiene todas las preguntas con sus opciones y temáticas.
     * Usando dos consultas separadas para evitar producto cartesiano.
     */
    @Query("SELECT DISTINCT p FROM PreguntaEntity p " +
           "LEFT JOIN FETCH p.opciones")
    List<PreguntaEntity> findAllWithOpciones();
    
    @Query("SELECT DISTINCT p FROM PreguntaEntity p " +
           "LEFT JOIN FETCH p.tematicas " +
           "WHERE p IN :preguntas")
    List<PreguntaEntity> findWithTematicas(@Param("preguntas") List<PreguntaEntity> preguntas);
    
    /**
     * Busca una pregunta por ID con todos sus detalles.
     */
    @Query("SELECT p FROM PreguntaEntity p " +
           "LEFT JOIN FETCH p.opciones " +
           "LEFT JOIN FETCH p.tematicas " +
           "WHERE p.id = :id")
    PreguntaEntity findByIdWithDetails(@Param("id") Long id);
}

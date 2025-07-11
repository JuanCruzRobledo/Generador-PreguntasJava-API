package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para temáticas.
 */
@Repository
public interface SpringDataTematicaRepository extends JpaRepository<TagTematicaEntity, Long> {
    
    /**
     * Busca una temática por su nombre.
     */
    Optional<TagTematicaEntity> findByNombre(String nombre);
    
    /**
     * Verifica si existe una temática con el nombre dado.
     */
    boolean existsByNombre(String nombre);


    @Query("SELECT c.tagsTematicas FROM CategoriaTematicaEntity c WHERE c.id = :categoriaId")
    Optional<List<TagTematicaEntity>> findTagsByCategoriaId(@Param("categoriaId") Long categoriaId);;
}

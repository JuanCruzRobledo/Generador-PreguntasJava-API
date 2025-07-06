package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TematicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para temáticas.
 */
@Repository
public interface SpringDataTematicaRepository extends JpaRepository<TematicaEntity, Long> {
    
    /**
     * Busca una temática por su nombre.
     */
    Optional<TematicaEntity> findByNombre(String nombre);
    
    /**
     * Verifica si existe una temática con el nombre dado.
     */
    boolean existsByNombre(String nombre);
}

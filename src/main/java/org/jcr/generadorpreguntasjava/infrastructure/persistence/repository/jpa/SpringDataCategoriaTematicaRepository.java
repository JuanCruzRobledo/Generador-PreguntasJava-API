package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.CategoriaTematicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataCategoriaTematicaRepository extends JpaRepository<CategoriaTematicaEntity, Long> {

    @Query("SELECT DISTINCT c FROM CategoriaTematicaEntity c JOIN c.lenguajes l WHERE l.id = :lenguajeId")
    List<CategoriaTematicaEntity> findByLenguajeId(@Param("lenguajeId") Long lenguajeId);
}

package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.CategoriaTematicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCategoriaTematicaRepository extends JpaRepository<CategoriaTematicaEntity, Long> {
}

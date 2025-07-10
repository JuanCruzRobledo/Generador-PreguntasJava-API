package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.ExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataExamenRepository extends JpaRepository<ExamenEntity, Long> {
}

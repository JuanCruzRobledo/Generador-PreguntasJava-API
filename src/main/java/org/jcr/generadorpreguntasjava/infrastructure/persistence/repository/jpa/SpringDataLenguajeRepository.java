package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.LenguajeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLenguajeRepository extends JpaRepository<LenguajeEntity, Long> {
}

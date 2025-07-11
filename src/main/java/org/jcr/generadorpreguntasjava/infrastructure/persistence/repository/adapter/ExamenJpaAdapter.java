package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Examen;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.ExamenEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.PreguntaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.ExamenPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataExamenRepository;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataPreguntaRepository;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataTematicaRepository;
import org.jcr.generadorpreguntasjava.port.out.ExamenRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador secundario que implementa el puerto de salida para persistencia de exámenes.
 *
 * Convierte entre objetos del dominio y entidades JPA.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class ExamenJpaAdapter implements ExamenRepositoryPort {

    private final SpringDataExamenRepository springDataExamenRepository;
    private final ExamenPersistenceMapper persistenceMapper;
    private final SpringDataPreguntaRepository preguntaJpaRepository;
    private final SpringDataTematicaRepository tematicaJpaRepository;

    @Override
    @Transactional
    public Examen guardar(Examen examen) {
        log.debug("Guardando examen: {}", examen.titulo());

        try {
            // 1️⃣ Convertir examen del dominio a entidad JPA
            ExamenEntity entity = persistenceMapper.toEntity(examen);

            // 2️⃣ Asegurar que las temáticas estén gestionadas
            Set<TagTematicaEntity> tematicas = entity.getTematicas();
            if (tematicas != null && !tematicas.isEmpty()) {
                Set<TagTematicaEntity> managedTematicas = tematicas.stream()
                        .map(t -> tematicaJpaRepository.findById(t.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Temática no encontrada con ID: " + t.getId())))
                        .collect(Collectors.toSet());
                entity.setTematicas(managedTematicas);
            }

            // 3️⃣ Asegurar que las preguntas estén gestionadas y relaciones bidireccionales establecidas
            List<PreguntaEntity> preguntas = entity.getPreguntas();
            if (preguntas != null && !preguntas.isEmpty()) {
                List<PreguntaEntity> managedPreguntas = preguntas.stream()
                        .map(p -> {
                            // Si la pregunta ya tiene ID, cargarla de la BD
                            if (p.getId() != null) {
                                return preguntaJpaRepository.findById(p.getId())
                                        .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada con ID: " + p.getId()));
                            } else {
                                // Nueva pregunta: establecer relaciones y guardar
                                if (p.getOpciones() != null) {
                                    p.getOpciones().forEach(o -> o.setPregunta(p));
                                }
                                return preguntaJpaRepository.save(p);
                            }
                        })
                        .toList();

                entity.setPreguntas(managedPreguntas);
            }

            // 4️⃣ Guardar examen usando Spring Data JPA
            ExamenEntity savedEntity = springDataExamenRepository.save(entity);

            // 5️⃣ Convertir de vuelta a dominio
            Examen savedExamen = persistenceMapper.toDomain(savedEntity);

            log.debug("Examen guardado exitosamente con ID: {}", savedEntity.getId());
            return savedExamen;

        } catch (Exception e) {
            log.error("Error al guardar examen: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar examen", e);
        }
    }
}
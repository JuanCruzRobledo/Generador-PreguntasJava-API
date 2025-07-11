package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Pregunta;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.OpcionEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.PreguntaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.PreguntaPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataPreguntaRepository;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataTematicaRepository;
import org.jcr.generadorpreguntasjava.port.out.PreguntaRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador secundario que implementa el puerto de salida para persistencia de preguntas.
 * 
 * Convierte entre objetos del dominio y entidades JPA.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class PreguntaJpaAdapter implements PreguntaRepositoryPort {
    
    private final SpringDataPreguntaRepository springDataRepository;
    private final PreguntaPersistenceMapper persistenceMapper;
    private final SpringDataTematicaRepository tematicaJpaRepository;

    @Override
    @Transactional
    public Pregunta guardar(Pregunta pregunta) {
        log.debug("Guardando pregunta: {}", pregunta.enunciado());

        try {
            // Convertir a entidad JPA
            PreguntaEntity entity = persistenceMapper.toEntity(pregunta);

            // ✅ Asegurar que las temáticas estén gestionadas
            Set<TagTematicaEntity> tematicas = entity.getTagsTematicas();
            if (tematicas != null && !tematicas.isEmpty()) {
                // Crear copia para evitar ConcurrentModificationException
                Set<TagTematicaEntity> tematicasCopia = new HashSet<>(tematicas);

                Set<TagTematicaEntity> managedTematicas = tematicasCopia.stream()
                        .map(t -> tematicaJpaRepository.findById(t.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Temática no encontrada con ID: " + t.getId())))
                        .collect(Collectors.toSet());

                entity.setTagsTematicas(managedTematicas);
            }

            // Establecer relaciones bidireccionales para opciones
            if (entity.getOpciones() != null) {
                for (OpcionEntity opcion : entity.getOpciones()) {
                    opcion.setPregunta(entity);
                }
            }

            // Guardar usando Spring Data JPA
            PreguntaEntity savedEntity = springDataRepository.save(entity);

            // Convertir de vuelta a dominio
            Pregunta savedPregunta = persistenceMapper.toDomain(savedEntity);

            log.debug("Pregunta guardada exitosamente con ID: {}", savedEntity.getId());
            return savedPregunta;

        } catch (Exception e) {
            log.error("Error al guardar pregunta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar pregunta", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Pregunta> buscarPorId(Long id) {
        log.debug("Buscando pregunta por ID: {}", id);
        
        try {
            PreguntaEntity entity = springDataRepository.findByIdWithDetails(id);
            
            if (entity == null) {
                log.debug("No se encontró pregunta con ID: {}", id);
                return Optional.empty();
            }
            
            Pregunta pregunta = persistenceMapper.toDomain(entity);
            log.debug("Pregunta encontrada con ID: {}", id);
            return Optional.of(pregunta);
            
        } catch (Exception e) {
            log.error("Error al buscar pregunta por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al buscar pregunta", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> obtenerTodas() {
        log.debug("Obteniendo todas las preguntas");
        
        try {
            // Primera consulta: obtener preguntas con opciones
            List<PreguntaEntity> preguntasConOpciones = springDataRepository.findAllWithOpciones();
            
            if (preguntasConOpciones.isEmpty()) {
                log.debug("No se encontraron preguntas");
                return List.of();
            }
            
            // Segunda consulta: obtener las mismas preguntas con temáticas
            List<PreguntaEntity> preguntasConTematicas = springDataRepository.findWithTematicas(preguntasConOpciones);

            // Combinar los resultados manualmente
            List<PreguntaEntity> preguntasCompletas = combinarPreguntasConDetalles(preguntasConOpciones, preguntasConTematicas);
            
            List<Pregunta> preguntas = persistenceMapper.toDomainList(preguntasCompletas);
            
            log.debug("Se obtuvieron {} preguntas", preguntas.size());
            return preguntas;
            
        } catch (Exception e) {
            log.error("Error al obtener todas las preguntas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener preguntas", e);
        }
    }
    
    /**
     * Combina las listas de preguntas con opciones y temáticas para evitar duplicados.
     */
    private List<PreguntaEntity> combinarPreguntasConDetalles(List<PreguntaEntity> preguntasConOpciones, 
                                                             List<PreguntaEntity> preguntasConTematicas) {
        // Crear un mapa de las preguntas con temáticas por ID para búsqueda rápida
        var tematicasPorId = preguntasConTematicas.stream()
                .collect(Collectors.toMap(PreguntaEntity::getId, p -> p.getTagsTematicas()));
        
        // Asignar las temáticas a las preguntas que ya tienen opciones
        return preguntasConOpciones.stream()
                .peek(p -> {
                    Set<TagTematicaEntity> tematicas = tematicasPorId.get(p.getId());
                    if (tematicas != null) {
                        p.setTagsTematicas(tematicas);
                    }
                })
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pregunta> buscarPorTematica(String nombreTematica) {
        log.debug("Buscando preguntas por temática: {}", nombreTematica);
        
        try {
            List<PreguntaEntity> entities = springDataRepository.findByTematicasNombre(nombreTematica);
            List<Pregunta> preguntas = persistenceMapper.toDomainList(entities);
            
            log.debug("Se encontraron {} preguntas para la temática '{}'", preguntas.size(), nombreTematica);
            return preguntas;
            
        } catch (Exception e) {
            log.error("Error al buscar preguntas por temática '{}': {}", nombreTematica, e.getMessage(), e);
            throw new RuntimeException("Error al buscar preguntas por temática", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeAlguna() {
        log.debug("Verificando si existe alguna pregunta");
        
        try {
            long count = springDataRepository.count();
            boolean existe = count > 0;
            
            log.debug("Existen {} preguntas en total", count);
            return existe;
            
        } catch (Exception e) {
            log.error("Error al verificar existencia de preguntas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al verificar existencia de preguntas", e);
        }
    }
}

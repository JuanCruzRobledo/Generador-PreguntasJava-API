package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.TagTematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.TematicaPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataTematicaRepository;
import org.jcr.generadorpreguntasjava.port.out.TematicaRepositoryPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador secundario que implementa el puerto de salida para persistencia de temáticas.
 * 
 * Convierte entre objetos del dominio y entidades JPA.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class TematicaJpaAdapter implements TematicaRepositoryPort {
    
    private final SpringDataTematicaRepository springDataRepository;
    private final TematicaPersistenceMapper persistenceMapper;
    
    @Override
    public TagTematica guardar(TagTematica tagTematica) {
        log.debug("Guardando temática: {}", tagTematica.nombre());
        
        try {
            // Convertir a entidad JPA
            TagTematicaEntity entity = persistenceMapper.toEntity(tagTematica);
            
            // Guardar usando Spring Data JPA
            TagTematicaEntity savedEntity = springDataRepository.save(entity);
            
            // Convertir de vuelta a dominio
            TagTematica savedTagTematica = persistenceMapper.toDomain(savedEntity);
            
            log.debug("Temática guardada exitosamente con ID: {}", savedEntity.getId());
            return savedTagTematica;
            
        } catch (Exception e) {
            log.error("Error al guardar temática '{}': {}", tagTematica.nombre(), e.getMessage(), e);
            throw new RuntimeException("Error al guardar temática", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TagTematica> buscarPorNombre(String nombre) {
        log.debug("Buscando temática por nombre: {}", nombre);
        
        try {
            Optional<TagTematicaEntity> entityOpt = springDataRepository.findByNombre(nombre);
            
            if (entityOpt.isEmpty()) {
                log.debug("No se encontró temática con nombre: {}", nombre);
                return Optional.empty();
            }
            
            TagTematica tagTematica = persistenceMapper.toDomain(entityOpt.get());
            log.debug("Temática encontrada con nombre: {}", nombre);
            return Optional.of(tagTematica);
            
        } catch (Exception e) {
            log.error("Error al buscar temática por nombre '{}': {}", nombre, e.getMessage(), e);
            throw new RuntimeException("Error al buscar temática", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagTematica> obtenerTodas() {
        log.debug("Obteniendo todas las temáticas");
        
        try {
            List<TagTematicaEntity> entities = springDataRepository.findAll();
            List<TagTematica> tagTematicas = persistenceMapper.toDomainTematicaList(entities);
            
            log.debug("Se obtuvieron {} temáticas", tagTematicas.size());
            return tagTematicas;
            
        } catch (Exception e) {
            log.error("Error al obtener todas las temáticas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener temáticas", e);
        }
    }

    @Override
    public List<TagTematica> obtenerTodosDeCategoria(Long id) {
        log.debug("Obteniendo todas las temáticas");

        try {
            List<TagTematicaEntity> entities = springDataRepository.findTagsByCategoriaId(id).orElseThrow(()-> new EntityNotFoundException("No se encontro la tags"));
            List<TagTematica> tagTematicas = persistenceMapper.toDomainTematicaList(entities);

            log.debug("Se obtuvieron {} temáticas", tagTematicas.size());
            return tagTematicas;

        } catch (Exception e) {
            log.error("Error al obtener todas las temáticas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener temáticas", e);
        }
    }

    /**
     * Persiste las temáticas, creando nuevas o actualizando existentes.
     */
    @Override
    @Transactional
    public TagTematica persistirConIntegridad(TagTematica tagTematica) {
        String nombreNormalizado = TagTematica.normalizarNombre(tagTematica.nombre());

        try {
            Optional<TagTematica> existente = buscarPorNombre(nombreNormalizado);

            if (existente.isPresent()) {
                // Solo incrementa si ya existía
                return guardar(existente.get().incrementarContador());
            } else {
                // Crea nueva con contador = 1
                return guardar(new TagTematica(
                        nombreNormalizado,
                        1, // Contador inicial
                        LocalDateTime.now()
                ));
            }
        } catch (DataIntegrityViolationException e) {
            log.warn("Conflicto de concurrencia al guardar temática {}", nombreNormalizado, e);
            return buscarPorNombre(nombreNormalizado)
                    .orElseThrow(() -> new IllegalStateException("Error al recuperar temática post-conflicto", e));
        }
    }
}

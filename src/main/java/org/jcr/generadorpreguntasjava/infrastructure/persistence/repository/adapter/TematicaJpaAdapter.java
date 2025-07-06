package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Tematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TematicaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.PersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataTematicaRepository;
import org.jcr.generadorpreguntasjava.port.out.TematicaRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    private final PersistenceMapper persistenceMapper;
    
    @Override
    public Tematica guardar(Tematica tematica) {
        log.debug("Guardando temática: {}", tematica.nombre());
        
        try {
            // Convertir a entidad JPA
            TematicaEntity entity = persistenceMapper.toEntity(tematica);
            
            // Guardar usando Spring Data JPA
            TematicaEntity savedEntity = springDataRepository.save(entity);
            
            // Convertir de vuelta a dominio
            Tematica savedTematica = persistenceMapper.toDomain(savedEntity);
            
            log.debug("Temática guardada exitosamente con ID: {}", savedEntity.getId());
            return savedTematica;
            
        } catch (Exception e) {
            log.error("Error al guardar temática '{}': {}", tematica.nombre(), e.getMessage(), e);
            throw new RuntimeException("Error al guardar temática", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Tematica> buscarPorNombre(String nombre) {
        log.debug("Buscando temática por nombre: {}", nombre);
        
        try {
            Optional<TematicaEntity> entityOpt = springDataRepository.findByNombre(nombre);
            
            if (entityOpt.isEmpty()) {
                log.debug("No se encontró temática con nombre: {}", nombre);
                return Optional.empty();
            }
            
            Tematica tematica = persistenceMapper.toDomain(entityOpt.get());
            log.debug("Temática encontrada con nombre: {}", nombre);
            return Optional.of(tematica);
            
        } catch (Exception e) {
            log.error("Error al buscar temática por nombre '{}': {}", nombre, e.getMessage(), e);
            throw new RuntimeException("Error al buscar temática", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Tematica> obtenerTodas() {
        log.debug("Obteniendo todas las temáticas");
        
        try {
            List<TematicaEntity> entities = springDataRepository.findAll();
            List<Tematica> tematicas = persistenceMapper.toDomainTematicaList(entities);
            
            log.debug("Se obtuvieron {} temáticas", tematicas.size());
            return tematicas;
            
        } catch (Exception e) {
            log.error("Error al obtener todas las temáticas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener temáticas", e);
        }
    }
}

package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Pregunta;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.OpcionEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.PreguntaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.PersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataPreguntaRepository;
import org.jcr.generadorpreguntasjava.port.out.PreguntaRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    private final PersistenceMapper persistenceMapper;
    
    @Override
    public Pregunta guardar(Pregunta pregunta) {
        log.debug("Guardando pregunta: {}", pregunta.enunciado());
        
        try {
            // Convertir a entidad JPA
            PreguntaEntity entity = persistenceMapper.toEntity(pregunta);
            
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
            List<PreguntaEntity> entities = springDataRepository.findAllWithDetails();
            List<Pregunta> preguntas = persistenceMapper.toDomainList(entities);
            
            log.debug("Se obtuvieron {} preguntas", preguntas.size());
            return preguntas;
            
        } catch (Exception e) {
            log.error("Error al obtener todas las preguntas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener preguntas", e);
        }
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

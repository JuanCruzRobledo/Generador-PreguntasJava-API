package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.SesionRespuesta;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.SesionRespuestaEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.PersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataSesionRespuestaRepository;
import org.jcr.generadorpreguntasjava.port.out.SesionRespuestaRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador secundario que implementa el puerto de salida para persistencia de sesiones de respuesta.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class SesionRespuestaJpaAdapter implements SesionRespuestaRepositoryPort {
    
    private final SpringDataSesionRespuestaRepository springDataRepository;
    private final PersistenceMapper persistenceMapper;
    
    @Override
    public SesionRespuesta guardar(SesionRespuesta sesion) {
        log.debug("Guardando sesión de respuesta para usuario: {}", sesion.usuarioId());
        
        try {
            SesionRespuestaEntity entity = persistenceMapper.toEntity(sesion);
            SesionRespuestaEntity savedEntity = springDataRepository.save(entity);
            SesionRespuesta savedSesion = persistenceMapper.toDomain(savedEntity);
            
            log.debug("Sesión guardada exitosamente con ID: {}", savedEntity.getId());
            return savedSesion;
            
        } catch (Exception e) {
            log.error("Error al guardar sesión de respuesta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar sesión de respuesta", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SesionRespuesta> buscarPorId(Long id) {
        log.debug("Buscando sesión por ID: {}", id);
        
        try {
            return springDataRepository.findById(id)
                .map(persistenceMapper::toDomain);
                
        } catch (Exception e) {
            log.error("Error al buscar sesión por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al buscar sesión", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerPorUsuario(Long usuarioId) {
        log.debug("Obteniendo sesiones por usuario: {}", usuarioId);
        
        try {
            List<SesionRespuestaEntity> entities = springDataRepository.findByUsuarioIdOrderByInicioRespuestaDesc(usuarioId);
            return persistenceMapper.toDomainSesionList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener sesiones por usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener sesiones", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerCompletadasPorUsuario(Long usuarioId) {
        log.debug("Obteniendo sesiones completadas por usuario: {}", usuarioId);
        
        try {
            List<SesionRespuestaEntity> entities = springDataRepository.findCompletadasByUsuarioId(usuarioId);
            return persistenceMapper.toDomainSesionList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener sesiones completadas por usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener sesiones completadas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerPorUsuarioYDificultad(Long usuarioId, Dificultad dificultad) {
        log.debug("Obteniendo sesiones por usuario {} y dificultad: {}", usuarioId, dificultad);
        
        try {
            List<SesionRespuestaEntity> entities = springDataRepository.findByUsuarioIdAndDificultad(usuarioId, dificultad.name());
            return persistenceMapper.toDomainSesionList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener sesiones por usuario {} y dificultad {}: {}", usuarioId, dificultad, e.getMessage(), e);
            throw new RuntimeException("Error al obtener sesiones por dificultad", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerPorUsuarioYTematica(Long usuarioId, String tematica) {
        log.debug("Obteniendo sesiones por usuario {} y temática: {}", usuarioId, tematica);
        
        try {
            List<SesionRespuestaEntity> entities = springDataRepository.findByUsuarioIdAndTematica(usuarioId, tematica);
            return persistenceMapper.toDomainSesionList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener sesiones por usuario {} y temática {}: {}", usuarioId, tematica, e.getMessage(), e);
            throw new RuntimeException("Error al obtener sesiones por temática", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerPorUsuarioYFechas(Long usuarioId, LocalDateTime desde, LocalDateTime hasta) {
        log.debug("Obteniendo sesiones por usuario {} entre {} y {}", usuarioId, desde, hasta);
        
        try {
            List<SesionRespuestaEntity> entities = springDataRepository.findByUsuarioIdAndFechas(usuarioId, desde, hasta);
            return persistenceMapper.toDomainSesionList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener sesiones por fechas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener sesiones por fechas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerEnProgresoPorUsuario(Long usuarioId) {
        log.debug("Obteniendo sesiones en progreso por usuario: {}", usuarioId);
        
        try {
            List<SesionRespuestaEntity> entities = springDataRepository.findEnProgresoByUsuarioId(usuarioId);
            return persistenceMapper.toDomainSesionList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener sesiones en progreso por usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener sesiones en progreso", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SesionRespuesta> buscarSesionEnProgreso(Long usuarioId, Long preguntaId) {
        log.debug("Buscando sesión en progreso para usuario {} y pregunta {}", usuarioId, preguntaId);
        
        try {
            return springDataRepository.findSesionEnProgreso(usuarioId, preguntaId)
                .map(persistenceMapper::toDomain);
                
        } catch (Exception e) {
            log.error("Error al buscar sesión en progreso: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar sesión en progreso", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarCompletadasPorUsuario(Long usuarioId) {
        log.debug("Contando sesiones completadas por usuario: {}", usuarioId);
        
        try {
            return springDataRepository.countCompletadasByUsuarioId(usuarioId);
            
        } catch (Exception e) {
            log.error("Error al contar sesiones completadas por usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al contar sesiones completadas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarCorrectasPorUsuario(Long usuarioId) {
        log.debug("Contando respuestas correctas por usuario: {}", usuarioId);
        
        try {
            return springDataRepository.countCorrectasByUsuarioId(usuarioId);
            
        } catch (Exception e) {
            log.error("Error al contar respuestas correctas por usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al contar respuestas correctas", e);
        }
    }
    
    @Override
    public int eliminarSesionesAbandonadas() {
        log.info("Eliminando sesiones abandonadas");
        
        try {
            LocalDateTime tiempoLimite = LocalDateTime.now().minusMinutes(30);
            int eliminadas = springDataRepository.eliminarSesionesAbandonadas(tiempoLimite);
            
            log.info("Se eliminaron {} sesiones abandonadas", eliminadas);
            return eliminadas;
            
        } catch (Exception e) {
            log.error("Error al eliminar sesiones abandonadas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar sesiones abandonadas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SesionRespuesta> obtenerUltimasSesiones(Long usuarioId, int limite) {
        log.debug("Obteniendo últimas {} sesiones para usuario: {}", limite, usuarioId);
        
        try {
            Pageable pageable = PageRequest.of(0, limite);
            List<SesionRespuestaEntity> entities = springDataRepository.findTopByUsuarioIdOrderByFinRespuestaDesc(usuarioId);
            
            // Aplicar límite manualmente ya que la query no lo soporta directamente
            List<SesionRespuestaEntity> limitedEntities = entities.stream()
                .limit(limite)
                .toList();
                
            return persistenceMapper.toDomainSesionList(limitedEntities);
            
        } catch (Exception e) {
            log.error("Error al obtener últimas sesiones para usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener últimas sesiones", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeSesion(Long usuarioId, Long preguntaId) {
        log.debug("Verificando existencia de sesión para usuario {} y pregunta {}", usuarioId, preguntaId);
        
        try {
            return springDataRepository.existsByUsuarioIdAndPreguntaId(usuarioId, preguntaId);
            
        } catch (Exception e) {
            log.error("Error al verificar existencia de sesión: {}", e.getMessage(), e);
            throw new RuntimeException("Error al verificar existencia de sesión", e);
        }
    }
}

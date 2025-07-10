package org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.UsuarioEntity;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper.UsuarioPersistenceMapper;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.repository.jpa.SpringDataUsuarioRepository;
import org.jcr.generadorpreguntasjava.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador secundario que implementa el puerto de salida para persistencia de usuarios.
 * Convierte entre objetos del dominio y entidades JPA.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class UsuarioJpaAdapter implements UsuarioRepositoryPort {
    
    private final SpringDataUsuarioRepository springDataRepository;
    private final UsuarioPersistenceMapper persistenceMapper;
    
    @Override
    public Usuario guardar(Usuario usuario) {
        log.debug("Guardando usuario: {}", usuario.email());
        
        try {
            UsuarioEntity entity = persistenceMapper.toEntity(usuario);
            UsuarioEntity savedEntity = springDataRepository.save(entity);
            Usuario savedUsuario = persistenceMapper.toDomain(savedEntity);
            
            log.debug("Usuario guardado exitosamente con ID: {}", savedEntity.getId());
            return savedUsuario;
            
        } catch (Exception e) {
            log.error("Error al guardar usuario {}: {}", usuario.email(), e.getMessage(), e);
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        log.debug("Buscando usuario por ID: {}", id);
        
        try {
            return springDataRepository.findById(id)
                .map(persistenceMapper::toDomain);
                
        } catch (Exception e) {
            log.error("Error al buscar usuario por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al buscar usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorGoogleId(String googleId) {
        log.debug("Buscando usuario por Google ID: {}", googleId);
        
        try {
            return springDataRepository.findByGoogleId(googleId)
                .map(persistenceMapper::toDomain);
                
        } catch (Exception e) {
            log.error("Error al buscar usuario por Google ID {}: {}", googleId, e.getMessage(), e);
            throw new RuntimeException("Error al buscar usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        
        try {
            return springDataRepository.findByEmail(email)
                .map(persistenceMapper::toDomain);
                
        } catch (Exception e) {
            log.error("Error al buscar usuario por email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error al buscar usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivos() {
        log.debug("Obteniendo usuarios activos");
        
        try {
            List<UsuarioEntity> entities = springDataRepository.findByActivoTrue();
            return persistenceMapper.toDomainUsuarioList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener usuarios activos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener usuarios activos", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        log.debug("Obteniendo todos los usuarios");
        
        try {
            List<UsuarioEntity> entities = springDataRepository.findAll();
            return persistenceMapper.toDomainUsuarioList(entities);
            
        } catch (Exception e) {
            log.error("Error al obtener todos los usuarios: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener usuarios", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existePorGoogleId(String googleId) {
        log.debug("Verificando existencia de usuario por Google ID: {}", googleId);
        
        try {
            return springDataRepository.existsByGoogleId(googleId);
            
        } catch (Exception e) {
            log.error("Error al verificar existencia por Google ID {}: {}", googleId, e.getMessage(), e);
            throw new RuntimeException("Error al verificar existencia de usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        log.debug("Verificando existencia de usuario por email: {}", email);
        
        try {
            return springDataRepository.existsByEmail(email);
            
        } catch (Exception e) {
            log.error("Error al verificar existencia por email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error al verificar existencia de usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarUsuarios() {
        log.debug("Contando total de usuarios");
        
        try {
            return springDataRepository.count();
            
        } catch (Exception e) {
            log.error("Error al contar usuarios: {}", e.getMessage(), e);
            throw new RuntimeException("Error al contar usuarios", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosActivos() {
        log.debug("Contando usuarios activos");
        
        try {
            return springDataRepository.countByActivoTrue();
            
        } catch (Exception e) {
            log.error("Error al contar usuarios activos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al contar usuarios activos", e);
        }
    }
    
    @Override
    public boolean desactivarUsuario(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        
        try {
            int updated = springDataRepository.desactivarUsuario(id);
            boolean desactivado = updated > 0;
            
            if (desactivado) {
                log.info("Usuario {} desactivado exitosamente", id);
            } else {
                log.warn("No se pudo desactivar el usuario {}", id);
            }
            
            return desactivado;
            
        } catch (Exception e) {
            log.error("Error al desactivar usuario {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al desactivar usuario", e);
        }
    }
    
    @Override
    public boolean activarUsuario(Long id) {
        log.info("Activando usuario con ID: {}", id);
        
        try {
            int updated = springDataRepository.activarUsuario(id);
            boolean activado = updated > 0;
            
            if (activado) {
                log.info("Usuario {} activado exitosamente", id);
            } else {
                log.warn("No se pudo activar el usuario {}", id);
            }
            
            return activado;
            
        } catch (Exception e) {
            log.error("Error al activar usuario {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al activar usuario", e);
        }
    }
}

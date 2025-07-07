package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.port.in.GestionarUsuarioPort;
import org.jcr.generadorpreguntasjava.port.out.UsuarioRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación para gestión de usuarios.
 * Implementa los casos de uso relacionados con usuarios y OAuth2.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService implements GestionarUsuarioPort {
    
    private final UsuarioRepositoryPort usuarioRepositoryPort;


    @Override
    public Usuario crearOActualizarUsuario(String googleId, String email, String nombre, String avatar) {
        log.info("Creando o actualizando usuario con Google ID: {}", googleId);
        
        if (googleId == null || googleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Google ID no puede estar vacío");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email no puede estar vacío");
        }
        
        try {
            // Buscar usuario existente por Google ID
            Optional<Usuario> usuarioExistente = usuarioRepositoryPort.buscarPorGoogleId(googleId);
            
            if (usuarioExistente.isPresent()) {
                // Actualizar usuario existente
                Usuario usuario = usuarioExistente.get();
                Usuario usuarioActualizado = usuario
                    .actualizarPerfil(nombre, avatar)
                    .actualizarUltimoAcceso();
                
                Usuario guardado = usuarioRepositoryPort.guardar(usuarioActualizado);
                log.info("Usuario actualizado exitosamente: {} (ID: {})", email, guardado.id());
                return guardado;
            } else {
                // Crear nuevo usuario
                Usuario nuevoUsuario = new Usuario(googleId, email, nombre, avatar);
                nuevoUsuario.validar();
                
                Usuario guardado = usuarioRepositoryPort.guardar(nuevoUsuario);
                log.info("Nuevo usuario creado exitosamente: {} (ID: {})", email, guardado.id());
                return guardado;
            }
            
        } catch (Exception e) {
            log.error("Error al crear/actualizar usuario con Google ID {}: {}", googleId, e.getMessage(), e);
            throw new RuntimeException("Error al gestionar usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorGoogleId(String googleId) {
        if (googleId == null || googleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Google ID no puede estar vacío");
        }
        
        log.debug("Buscando usuario por Google ID: {}", googleId);
        return usuarioRepositoryPort.buscarPorGoogleId(googleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo");
        }
        
        log.debug("Buscando usuario por ID: {}", id);
        return usuarioRepositoryPort.buscarPorId(id);
    }
    
    @Override
    public Usuario actualizarUltimoAcceso(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID del usuario no puede ser nulo");
        }
        
        log.debug("Actualizando último acceso del usuario: {}", usuarioId);
        
        try {
            Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            Usuario usuarioActualizado = usuario.actualizarUltimoAcceso();
            return usuarioRepositoryPort.guardar(usuarioActualizado);
            
        } catch (Exception e) {
            log.error("Error al actualizar último acceso del usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al actualizar último acceso", e);
        }
    }
    
    @Override
    public Usuario actualizarPerfil(Long usuarioId, String nuevoNombre, String nuevoAvatar) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID del usuario no puede ser nulo");
        }
        
        log.info("Actualizando perfil del usuario: {}", usuarioId);
        
        try {
            Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            Usuario usuarioActualizado = usuario.actualizarPerfil(nuevoNombre, nuevoAvatar);
            usuarioActualizado.validar();
            
            Usuario guardado = usuarioRepositoryPort.guardar(usuarioActualizado);
            log.info("Perfil actualizado exitosamente para usuario: {}", usuarioId);
            return guardado;
            
        } catch (Exception e) {
            log.error("Error al actualizar perfil del usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al actualizar perfil", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivos() {
        log.debug("Obteniendo usuarios activos");
        return usuarioRepositoryPort.obtenerUsuariosActivos();
    }
    
    @Override
    public boolean desactivarUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID del usuario no puede ser nulo");
        }
        
        log.info("Desactivando usuario: {}", usuarioId);
        
        try {
            // Verificar que el usuario existe
            usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            boolean desactivado = usuarioRepositoryPort.desactivarUsuario(usuarioId);
            
            if (desactivado) {
                log.info("Usuario desactivado exitosamente: {}", usuarioId);
            } else {
                log.warn("No se pudo desactivar el usuario: {}", usuarioId);
            }
            
            return desactivado;
            
        } catch (Exception e) {
            log.error("Error al desactivar usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al desactivar usuario", e);
        }
    }
    
    @Override
    public boolean activarUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID del usuario no puede ser nulo");
        }
        
        log.info("Activando usuario: {}", usuarioId);
        
        try {
            // Verificar que el usuario existe
            usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            boolean activado = usuarioRepositoryPort.activarUsuario(usuarioId);
            
            if (activado) {
                log.info("Usuario activado exitosamente: {}", usuarioId);
            } else {
                log.warn("No se pudo activar el usuario: {}", usuarioId);
            }
            
            return activado;
            
        } catch (Exception e) {
            log.error("Error al activar usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al activar usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean puedeUsarSistema(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID del usuario no puede ser nulo");
        }
        
        try {
            Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
            return usuario.puedeUsarSistema();
            
        } catch (Exception e) {
            log.error("Error al verificar si el usuario {} puede usar el sistema: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al verificar permisos de usuario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasUsuarios obtenerEstadisticasUsuarios() {
        log.debug("Obteniendo estadísticas de usuarios");
        
        try {
            long totalUsuarios = usuarioRepositoryPort.contarUsuarios();
            long usuariosActivos = usuarioRepositoryPort.contarUsuariosActivos();
            long usuariosInactivos = totalUsuarios - usuariosActivos;
            
            return new EstadisticasUsuarios(totalUsuarios, usuariosActivos, usuariosInactivos);
            
        } catch (Exception e) {
            log.error("Error al obtener estadísticas de usuarios: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener estadísticas de usuarios", e);
        }
    }

    @Override
    public Usuario obtenerUsuarioActual() {
        // Este es un ejemplo si usás Spring Security con OAuth2
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay un usuario autenticado");
        }

        String email = authentication.getName(); // o extraé el googleId desde el principal

        return usuarioRepositoryPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Override
    public Usuario crearUsuarioAnonimo() {
        String uuid = UUID.randomUUID().toString();
        String emailAnonimo = "anonimo+" + uuid + "@example.com";
        Usuario anonimo = new Usuario("anon-" + uuid, emailAnonimo, "Anónimo", null);
        return usuarioRepositoryPort.guardar(anonimo);
    }
}

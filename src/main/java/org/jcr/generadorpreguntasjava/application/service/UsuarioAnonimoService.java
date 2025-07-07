package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para manejar un usuario anónimo por defecto.
 * Permite usar el sistema sin autenticación durante el desarrollo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioAnonimoService {
    
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    
    private static final String GOOGLE_ID_ANONIMO = "anonymous-user";
    private static final String EMAIL_ANONIMO = "anonymous@localhost.com";
    private static final String NOMBRE_ANONIMO = "Usuario Anónimo";
    private static final String AVATAR_ANONIMO = null;
    
    /**
     * Obtiene o crea el usuario anónimo por defecto.
     */
    public Usuario obtenerUsuarioAnonimo() {
        log.debug("Obteniendo usuario anónimo");
        
        Optional<Usuario> usuarioExistente = usuarioRepositoryPort.buscarPorGoogleId(GOOGLE_ID_ANONIMO);
        
        if (usuarioExistente.isPresent()) {
            return usuarioExistente.get();
        }
        
        // Crear usuario anónimo
        Usuario usuarioAnonimo = new Usuario(
            GOOGLE_ID_ANONIMO, 
            EMAIL_ANONIMO, 
            NOMBRE_ANONIMO, 
            AVATAR_ANONIMO
        );
        
        Usuario usuarioGuardado = usuarioRepositoryPort.guardar(usuarioAnonimo);
        
        log.info("Usuario anónimo creado con ID: {}", usuarioGuardado.id());
        return usuarioGuardado;
    }
    
    /**
     * Obtiene el ID del usuario anónimo.
     */
    public Long obtenerIdUsuarioAnonimo() {
        return obtenerUsuarioAnonimo().id();
    }
    
    /**
     * Verifica si un usuario es el usuario anónimo.
     */
    public boolean esUsuarioAnonimo(Long usuarioId) {
        if (usuarioId == null) return false;
        
        return usuarioRepositoryPort.buscarPorId(usuarioId)
            .map(usuario -> GOOGLE_ID_ANONIMO.equals(usuario.googleId()))
            .orElse(false);
    }
    
    /**
     * Verifica si un usuario por Google ID es el usuario anónimo.
     */
    public boolean esUsuarioAnonimo(String googleId) {
        return GOOGLE_ID_ANONIMO.equals(googleId);
    }
}

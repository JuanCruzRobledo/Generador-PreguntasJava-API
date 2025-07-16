package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.infrastructure.security.service.JwtService;
import org.jcr.generadorpreguntasjava.port.out.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de autenticación en el dominio.
 * Implementa la lógica de negocio para autenticación y autorización.
 * Sigue los principios de la arquitectura hexagonal.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {
    
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final JwtService jwtService;
    
    /**
     * Autentica un usuario con email y contraseña.
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Usuario autenticado si las credenciales son válidas
     * @throws AuthenticationException Si las credenciales son inválidas
     */
    public Usuario authenticate(String email, String password) {
        log.info("Intentando autenticar usuario: {}", email);
        
        // Validar parámetros de entrada
        if (email == null || email.trim().isEmpty()) {
            log.warn("Intento de autenticación con email vacío");
            throw new AuthenticationException("El email no puede estar vacío");
        }
        
        if (password == null || password.trim().isEmpty()) {
            log.warn("Intento de autenticación con contraseña vacía para email: {}", email);
            throw new AuthenticationException("La contraseña no puede estar vacía");
        }
        
        // Buscar usuario por email
        Optional<Usuario> usuarioOptional = usuarioRepositoryPort.buscarPorEmail(email.trim().toLowerCase());
        
        if (usuarioOptional.isEmpty()) {
            log.warn("Usuario no encontrado: {}", email);
            throw new AuthenticationException("Credenciales inválidas");
        }
        
        Usuario usuario = usuarioOptional.get();
        
        // Verificar que el usuario esté activo
        if (!usuario.puedeUsarSistema()) {
            log.warn("Usuario inactivo intentando autenticarse: {}", email);
            throw new AuthenticationException("Usuario inactivo");
        }
        
        // Verificar contraseña (por ahora simulamos validación)
        // En una implementación real, verificarías la contraseña hasheada
        if (!isValidPassword(password, usuario)) {
            log.warn("Contraseña inválida para usuario: {}", email);
            throw new AuthenticationException("Credenciales inválidas");
        }
        
        // Actualizar último acceso
        Usuario usuarioActualizado = usuario.actualizarUltimoAcceso();
        usuarioRepositoryPort.guardar(usuarioActualizado);
        
        log.info("Usuario autenticado exitosamente: {}", email);
        return usuarioActualizado;
    }

    /**
     * Genera un usuario anonimo
     *
     * @return Usuario anonimo
     */
    public Usuario crearUsuarioAnonimo() {
        String uuid = UUID.randomUUID().toString();
        String emailAnonimo = "anonimo+" + uuid + "@example.com";
        Usuario anonimo = new Usuario("anon-" + uuid, emailAnonimo, "Anónimo", null);
        return usuarioRepositoryPort.guardar(anonimo);
    }
    
    /**
     * Genera tokens JWT para un usuario autenticado.
     * 
     * @param usuario Usuario para el cual generar tokens
     * @return Record con access token y refresh token
     */
    public AuthTokens generateTokens(Usuario usuario) {
        log.debug("Generando tokens para usuario: {}", usuario.email());
        
        String accessToken = jwtService.generateAccessToken(
            usuario.id(), 
            usuario.email(), 
            usuario.nombre()
        );
        
        String refreshToken = jwtService.generateRefreshToken(
            usuario.id(), 
            usuario.email()
        );
        
        return new AuthTokens(accessToken, refreshToken);
    }
    
    /**
     * Renueva un access token usando un refresh token válido.
     * 
     * @param refreshToken Token de refresh
     * @return Nuevo access token
     * @throws AuthenticationException Si el refresh token es inválido
     */
    public String refreshAccessToken(String refreshToken) {
        log.debug("Renovando access token");
        
        // Validar refresh token
        if (!jwtService.isValidRefreshToken(refreshToken)) {
            log.warn("Intento de renovación con refresh token inválido");
            throw new AuthenticationException("Refresh token inválido");
        }
        
        // Extraer información del usuario
        String email = jwtService.extractEmail(refreshToken);
        Long userId = jwtService.extractUserId(refreshToken);
        
        // Verificar que el usuario aún existe y está activo
        Optional<Usuario> usuarioOptional = usuarioRepositoryPort.buscarPorEmail(email);
        if (usuarioOptional.isEmpty()) {
            log.warn("Usuario no encontrado durante renovación de token: {}", email);
            throw new AuthenticationException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOptional.get();
        if (!usuario.puedeUsarSistema()) {
            log.warn("Usuario inactivo durante renovación de token: {}", email);
            throw new AuthenticationException("Usuario inactivo");
        }
        
        // Generar nuevo access token
        String newAccessToken = jwtService.generateAccessToken(
            userId, 
            email, 
            usuario.nombre()
        );
        
        log.info("Access token renovado exitosamente para usuario: {}", email);
        return newAccessToken;
    }
    
    /**
     * Invalida los tokens de un usuario (logout).
     * 
     * @param accessToken Token de acceso a invalidar
     */
    public void logout(String accessToken) {
        try {
            String email = jwtService.extractEmail(accessToken);
            log.info("Cerrando sesión para usuario: {}", email);
            
            // En una implementación más completa, podrías mantener una lista negra de tokens
            // Por ahora, simplemente logeamos el logout
            
        } catch (Exception e) {
            log.debug("Error al procesar logout con token: {}", e.getMessage());
        }
    }
    
    /**
     * Obtiene un usuario basado en un access token.
     * 
     * @param accessToken Token de acceso
     * @return Usuario si el token es válido
     * @throws AuthenticationException Si el token es inválido
     */
    public Usuario getUserFromToken(String accessToken) {
        if (!jwtService.isValidAccessToken(accessToken)) {
            throw new AuthenticationException("Token inválido");
        }
        
        String email = jwtService.extractEmail(accessToken);
        return usuarioRepositoryPort.buscarPorEmail(email)
            .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));
    }
    
    /**
     * Obtiene o registra un usuario OAuth2 a partir de la información de autenticación.
     * Si el usuario ya existe, actualiza su información.
     * Si no existe, crea un nuevo usuario.
     * 
     * @param authentication Información de autenticación OAuth2
     * @return Usuario registrado o actualizado
     */
    public Optional<Usuario> getOrRegisterOauth2User(Authentication authentication) {
        log.info("Procesando usuario OAuth2: {}", authentication.getName());
        
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            log.warn("Principal no es un OAuth2User");
            return Optional.empty();
        }
        
        // Extraer información del usuario OAuth2
        String email = oauth2User.getAttribute("email");
        String nombre = oauth2User.getAttribute("name");
        String avatar = oauth2User.getAttribute("picture");
        String googleId = oauth2User.getAttribute("sub");
        
        if (email == null || email.trim().isEmpty()) {
            log.warn("Email no disponible en OAuth2User");
            return Optional.empty();
        }
        
        email = email.trim().toLowerCase();
        
        // Buscar usuario existente por email
        Optional<Usuario> usuarioExistente = usuarioRepositoryPort.buscarPorEmail(email);
        
        if (usuarioExistente.isPresent()) {
            // Actualizar información del usuario existente
            Usuario usuario = usuarioExistente.get();
            
            // Actualizar avatar si es diferente
            if (avatar != null && !avatar.equals(usuario.avatar())) {
                usuario = usuario.actualizarAvatar(avatar);
            }
            
            // Actualizar último acceso
            usuario = usuario.actualizarUltimoAcceso();
            
            // Guardar cambios
            usuarioRepositoryPort.guardar(usuario);
            
            log.info("Usuario OAuth2 actualizado: {}", email);
            return Optional.of(usuario);
        } else {
            // Crear nuevo usuario
            Usuario nuevoUsuario = Usuario.crearUsuarioOAuth2ConGoogleId(
                googleId,
                email,
                nombre != null ? nombre : "Usuario OAuth2",
                avatar
            );
            
            // Guardar nuevo usuario
            Usuario usuarioGuardado = usuarioRepositoryPort.guardar(nuevoUsuario);
            
            log.info("Nuevo usuario OAuth2 registrado: {}", email);
            return Optional.of(usuarioGuardado);
        }
    }
    
    /**
     * Verifica si una contraseña es válida para un usuario.
     * En una implementación real, esto verificaría el hash de la contraseña.
     * 
     * @param password Contraseña a verificar
     * @param usuario Usuario para verificar
     * @return true si la contraseña es válida
     */
    private boolean isValidPassword(String password, Usuario usuario) {
        // Implementación simplificada para desarrollo
        // En producción, verificar contra un hash almacenado
        return password.length() >= 6;
    }
    
    /**
     * Record para encapsular los tokens de autenticación.
     */
    public record AuthTokens(String accessToken, String refreshToken) {}
    
    /**
     * Excepción personalizada para errores de autenticación.
     */
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
        
        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

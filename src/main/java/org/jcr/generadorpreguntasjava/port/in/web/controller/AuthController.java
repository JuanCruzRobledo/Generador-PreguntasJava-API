package org.jcr.generadorpreguntasjava.port.in.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.application.service.AuthenticationService;
import org.jcr.generadorpreguntasjava.domain.model.Usuario;
import org.jcr.generadorpreguntasjava.infrastructure.security.service.CookieService;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.AuthResponse;
import org.jcr.generadorpreguntasjava.port.in.web.dto.request.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para endpoints de autenticación.
 * Expone los endpoints públicos de autenticación sin acoplarse al dominio.
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    
    /**
     * Endpoint POST /auth/login
     * Autentica un usuario con email y contraseña.
     * Devuelve access token y refresh token en cookies HttpOnly y Secure.
     * 
     * @param loginRequest Credenciales del usuario
     * @param response Respuesta HTTP para configurar cookies
     * @return Información del usuario autenticado
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        
        log.info("Intento de login para usuario: {}", loginRequest.email());
        
        try {
            // Autenticar usuario
            Usuario usuario = authenticationService.authenticate(
                loginRequest.email(), 
                loginRequest.password()
            );
            
            // Generar tokens
            AuthenticationService.AuthTokens tokens = authenticationService.generateTokens(usuario);
            
            // Configurar cookies con tokens
            cookieService.createAccessTokenCookie(response, tokens.accessToken());
            cookieService.createRefreshTokenCookie(response, tokens.refreshToken());
            
            // Crear respuesta exitosa
            AuthResponse authResponse = AuthResponse.success(
                usuario.id(),
                usuario.email(),
                usuario.nombre(),
                usuario.avatar()
            );
            
            log.info("Login exitoso para usuario: {}", loginRequest.email());
            return ResponseEntity.ok(authResponse);
            
        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Fallo en autenticación para usuario {}: {}", loginRequest.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.error(e.getMessage()));
                
        } catch (Exception e) {
            log.error("Error interno durante login para usuario {}: {}", 
                loginRequest.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error interno del servidor"));
        }
    }
    
    /**
     * Endpoint POST /auth/refresh
     * Lee el refresh token desde la cookie y devuelve un nuevo access token.
     * También devuelve el nuevo access token en cookie HttpOnly y Secure.
     * 
     * @param request Petición HTTP para leer cookies
     * @param response Respuesta HTTP para configurar cookies
     * @return Información del usuario con token renovado
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.debug("Intento de refresh token");
        
        try {
            // Obtener refresh token desde cookie
            String refreshToken = cookieService.getRefreshTokenFromCookies(request)
                .orElseThrow(() -> new AuthenticationService.AuthenticationException(
                    "Refresh token no encontrado"));
            
            // Renovar access token
            String newAccessToken = authenticationService.refreshAccessToken(refreshToken);
            
            // Configurar nueva cookie con el access token renovado
            cookieService.createAccessTokenCookie(response, newAccessToken);
            
            // Obtener información del usuario desde el token renovado
            Usuario usuario = authenticationService.getUserFromToken(newAccessToken);
            
            // Crear respuesta exitosa
            AuthResponse authResponse = AuthResponse.success(
                usuario.id(),
                usuario.email(),
                usuario.nombre(),
                usuario.avatar()
            );
            
            log.info("Token renovado exitosamente para usuario: {}", usuario.email());
            return ResponseEntity.ok(authResponse);
            
        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Fallo en renovación de token: {}", e.getMessage());
            
            // Limpiar cookies en caso de error
            cookieService.clearAuthenticationCookies(response);
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.error(e.getMessage()));
                
        } catch (Exception e) {
            log.error("Error interno durante refresh token: {}", e.getMessage(), e);
            
            // Limpiar cookies en caso de error
            cookieService.clearAuthenticationCookies(response);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error interno del servidor"));
        }
    }
    
    /**
     * Endpoint POST /auth/logout
     * Invalida las cookies de tokens de autenticación.
     * 
     * @param request Petición HTTP para leer cookies
     * @param response Respuesta HTTP para limpiar cookies
     * @return Confirmación de logout
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.debug("Intento de logout");
        
        try {
            // Obtener access token desde cookie para logging
            cookieService.getAccessTokenFromCookies(request)
                .ifPresent(accessToken -> {
                    try {
                        authenticationService.logout(accessToken);
                    } catch (Exception e) {
                        log.debug("Error al procesar logout: {}", e.getMessage());
                    }
                });
            
            // Limpiar cookies de autenticación
            cookieService.clearAuthenticationCookies(response);
            
            log.info("Logout exitoso");
            return ResponseEntity.ok(AuthResponse.success(null, null, null, null));
            
        } catch (Exception e) {
            log.error("Error interno durante logout: {}", e.getMessage(), e);
            
            // Limpiar cookies incluso si hay error
            cookieService.clearAuthenticationCookies(response);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error interno del servidor"));
        }
    }
    
    /**
     * Endpoint GET /auth/me
     * Obtiene información del usuario autenticado actual.
     * 
     * @param request Petición HTTP para leer cookies
     * @return Información del usuario actual
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpServletRequest request) {
        
        log.debug("Obteniendo información del usuario actual");
        
        try {
            // Obtener access token desde cookie
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                .orElseThrow(() -> new AuthenticationService.AuthenticationException(
                    "Access token no encontrado"));
            
            // Obtener usuario desde token
            Usuario usuario = authenticationService.getUserFromToken(accessToken);
            
            // Crear respuesta exitosa
            AuthResponse authResponse = AuthResponse.success(
                usuario.id(),
                usuario.email(),
                usuario.nombre(),
                usuario.avatar()
            );
            
            return ResponseEntity.ok(authResponse);
            
        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Token inválido o expirado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.error(e.getMessage()));
                
        } catch (Exception e) {
            log.error("Error interno obteniendo usuario actual: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error interno del servidor"));
        }
    }
    
    /**
     * Endpoint GET /auth/status
     * Verifica el estado de autenticación sin devolver información sensible.
     * 
     * @param request Petición HTTP para leer cookies
     * @return Estado de autenticación
     */
    @GetMapping("/status")
    public ResponseEntity<AuthResponse> getAuthStatus(HttpServletRequest request) {
        
        try {
            // Verificar si existe access token válido
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                .orElse(null);
            
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error("No autenticado"));
            }
            
            // Verificar validez del token
            authenticationService.getUserFromToken(accessToken);
            
            return ResponseEntity.ok(AuthResponse.success(null, null, null, null));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.error("Token inválido"));
        }
    }
    
    /**
     * Endpoint GET /auth/oauth2/success
     * Maneja la respuesta exitosa después de la autenticación OAuth2.
     * 
     * @param request Petición HTTP para leer cookies
     * @return Información del usuario autenticado con OAuth2
     */
    @GetMapping("/oauth2/success")
    public ResponseEntity<AuthResponse> oauth2Success(HttpServletRequest request) {
        
        log.debug("Procesando éxito de OAuth2");
        
        try {
            // Obtener access token desde cookie (debería estar establecido por el OAuth2SuccessHandler)
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                .orElseThrow(() -> new AuthenticationService.AuthenticationException(
                    "Access token no encontrado después de OAuth2"));
            
            // Obtener usuario desde token
            Usuario usuario = authenticationService.getUserFromToken(accessToken);
            
            // Crear respuesta exitosa
            AuthResponse authResponse = AuthResponse.success(
                usuario.id(),
                usuario.email(),
                usuario.nombre(),
                usuario.avatar()
            );
            
            log.info("OAuth2 login exitoso para usuario: {}", usuario.email());
            return ResponseEntity.ok(authResponse);
            
        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Fallo en OAuth2 success: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.error(e.getMessage()));
                
        } catch (Exception e) {
            log.error("Error interno en OAuth2 success: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error interno del servidor"));
        }
    }
    
    /**
     * Endpoint GET /auth/oauth2/failure
     * Maneja errores en la autenticación OAuth2.
     * 
     * @param request Petición HTTP
     * @return Respuesta de error
     */
    @GetMapping("/oauth2/failure")
    public ResponseEntity<AuthResponse> oauth2Failure(HttpServletRequest request) {
        
        log.warn("Fallo en autenticación OAuth2");
        
        // Obtener parámetro de error si existe
        String error = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");
        
        String errorMessage = "Error en autenticación OAuth2";
        if (error != null) {
            errorMessage = error;
            if (errorDescription != null) {
                errorMessage += ": " + errorDescription;
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(AuthResponse.error(errorMessage));
    }
}

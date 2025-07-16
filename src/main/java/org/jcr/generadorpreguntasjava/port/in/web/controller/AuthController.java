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
import org.jcr.generadorpreguntasjava.shared.response.ApiResponse;
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
     * POST /auth/login
     * Autentica usuario y devuelve tokens en cookies HttpOnly/Secure.
     * Respuesta: ApiResponse<AuthResponse>
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        log.info("Intento de login para usuario: {}", loginRequest.email());

        try {
            Usuario usuario = authenticationService.authenticate(
                    loginRequest.email(),
                    loginRequest.password()
            );

            AuthenticationService.AuthTokens tokens = authenticationService.generateTokens(usuario);

            cookieService.createAccessTokenCookie(response, tokens.accessToken());
            cookieService.createRefreshTokenCookie(response, tokens.refreshToken());

            AuthResponse authResponse = AuthResponse.success(
                    usuario.id(),
                    usuario.email(),
                    usuario.nombre(),
                    usuario.avatar()
            );

            log.info("Login exitoso para usuario: {}", loginRequest.email());
            return ResponseEntity.ok(ApiResponse.exito(authResponse, "Autenticación exitosa"));

        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Fallo en autenticación para usuario {}: {}", loginRequest.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No autorizado", e.getMessage()));

        } catch (Exception e) {
            log.error("Error interno durante login para usuario {}: {}", loginRequest.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    /**
     * POST /auth/refresh
     * Renueva access token usando refresh token en cookie.
     * Respuesta: ApiResponse<AuthResponse>
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.debug("Intento de refresh token");

        try {
            String refreshToken = cookieService.getRefreshTokenFromCookies(request)
                    .orElseThrow(() -> new AuthenticationService.AuthenticationException("Refresh token no encontrado"));

            String newAccessToken = authenticationService.refreshAccessToken(refreshToken);

            cookieService.createAccessTokenCookie(response, newAccessToken);

            Usuario usuario = authenticationService.getUserFromToken(newAccessToken);

            AuthResponse authResponse = AuthResponse.success(
                    usuario.id(),
                    usuario.email(),
                    usuario.nombre(),
                    usuario.avatar()
            );

            log.info("Token renovado exitosamente para usuario: {}", usuario.email());
            return ResponseEntity.ok(ApiResponse.exito(authResponse, "Token renovado exitosamente"));

        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Fallo en renovación de token: {}", e.getMessage());
            cookieService.clearAuthenticationCookies(response);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No autorizado", e.getMessage()));

        } catch (Exception e) {
            log.error("Error interno durante refresh token: {}", e.getMessage(), e);
            cookieService.clearAuthenticationCookies(response);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    /**
     * POST /auth/logout
     * Invalida tokens y limpia cookies.
     * Respuesta: ApiResponse<AuthResponse>
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<AuthResponse>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.debug("Intento de logout");

        try {
            cookieService.getAccessTokenFromCookies(request)
                    .ifPresent(accessToken -> {
                        try {
                            authenticationService.logout(accessToken);
                        } catch (Exception e) {
                            log.debug("Error al procesar logout: {}", e.getMessage());
                        }
                    });

            cookieService.clearAuthenticationCookies(response);

            log.info("Logout exitoso");
            // Respuesta sin datos, solo mensaje de éxito
            return ResponseEntity.ok(ApiResponse.exito(null, "Logout exitoso"));

        } catch (Exception e) {
            log.error("Error interno durante logout: {}", e.getMessage(), e);
            cookieService.clearAuthenticationCookies(response);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    /**
     * GET /auth/me
     * Devuelve info del usuario actual basado en token.
     * Respuesta: ApiResponse<AuthResponse>
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(HttpServletRequest request) {
        log.debug("Obteniendo información del usuario actual");

        try {
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                    .orElseThrow(() -> new AuthenticationService.AuthenticationException("Access token no encontrado"));

            Usuario usuario = authenticationService.getUserFromToken(accessToken);

            AuthResponse authResponse = AuthResponse.success(
                    usuario.id(),
                    usuario.email(),
                    usuario.nombre(),
                    usuario.avatar()
            );

            return ResponseEntity.ok(ApiResponse.exito(authResponse, "Usuario autenticado"));

        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Token inválido o expirado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No autorizado", e.getMessage()));

        } catch (Exception e) {
            log.error("Error interno obteniendo usuario actual: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    /**
     * GET /auth/status
     * Verifica si el usuario está autenticado (token válido).
     * Respuesta: ApiResponse<AuthResponse>
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<AuthResponse>> getAuthStatus(HttpServletRequest request) {
        try {
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                    .orElse(null);

            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("No autenticado", "No hay token"));
            }

            authenticationService.getUserFromToken(accessToken);

            return ResponseEntity.ok(ApiResponse.exito(null, "Usuario autenticado"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No autorizado", "Token inválido o expirado"));
        }
    }

    /**
     * GET /auth/oauth2/success
     * Maneja éxito OAuth2, devuelve info usuario con tokens en cookies.
     * Respuesta: ApiResponse<AuthResponse>
     */
    @GetMapping("/oauth2/success")
    public ResponseEntity<ApiResponse<AuthResponse>> oauth2Success(HttpServletRequest request) {
        log.debug("Procesando éxito de OAuth2");

        try {
            String accessToken = cookieService.getAccessTokenFromCookies(request)
                    .orElseThrow(() -> new AuthenticationService.AuthenticationException("Access token no encontrado después de OAuth2"));

            Usuario usuario = authenticationService.getUserFromToken(accessToken);

            AuthResponse authResponse = AuthResponse.success(
                    usuario.id(),
                    usuario.email(),
                    usuario.nombre(),
                    usuario.avatar()
            );

            log.info("OAuth2 login exitoso para usuario: {}", usuario.email());
            return ResponseEntity.ok(ApiResponse.exito(authResponse, "OAuth2 autenticación exitosa"));

        } catch (AuthenticationService.AuthenticationException e) {
            log.warn("Fallo en OAuth2 success: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No autorizado", e.getMessage()));

        } catch (Exception e) {
            log.error("Error interno en OAuth2 success: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor", e.getMessage()));
        }
    }

    /**
     * GET /auth/oauth2/failure
     * Maneja errores de OAuth2 y devuelve mensaje de error estandarizado.
     * Respuesta: ApiResponse<AuthResponse>
     */
    @GetMapping("/oauth2/failure")
    public ResponseEntity<ApiResponse<AuthResponse>> oauth2Failure(HttpServletRequest request) {
        log.warn("Fallo en autenticación OAuth2");

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
                .body(ApiResponse.error(errorMessage, "OAuth2 error"));
    }

    /**
     * POST /auth/invitado
     * Crea un usuario anónimo y devuelve AuthResponse con tokens en cookies
     */
    @PostMapping("/invitado")
    public ResponseEntity<ApiResponse<AuthResponse>> loginAnonimo(HttpServletResponse response) {
        log.info("Creando usuario anónimo y generando sesión");

        try {
            // Crear usuario anónimo
            Usuario anonimo = authenticationService.crearUsuarioAnonimo();

            // Generar tokens de autenticación
            AuthenticationService.AuthTokens tokens = authenticationService.generateTokens(anonimo);

            // Setear ambos tokens en cookies HttpOnly
            cookieService.createAccessTokenCookie(response, tokens.accessToken());
            cookieService.createRefreshTokenCookie(response, tokens.refreshToken());

            // Armar respuesta con info de usuario
            AuthResponse authResponse = AuthResponse.success(
                    anonimo.id(),
                    anonimo.email(),
                    anonimo.nombre(),
                    anonimo.avatar()
            );

            log.info("Usuario anónimo autenticado correctamente: {}", anonimo.id());
            return ResponseEntity.ok(ApiResponse.exito(authResponse, "Sesión iniciada como invitado"));

        } catch (Exception e) {
            log.error("Error al crear usuario anónimo autenticado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("No se pudo iniciar como invitado", e.getMessage()));
        }
    }
}

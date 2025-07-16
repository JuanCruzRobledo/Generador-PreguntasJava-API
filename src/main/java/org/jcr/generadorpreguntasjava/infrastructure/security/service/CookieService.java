package org.jcr.generadorpreguntasjava.infrastructure.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.infrastructure.security.config.CookieProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Servicio para manejo de cookies de autenticación.
 * Implementa la lógica para crear, leer y eliminar cookies de tokens JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CookieService {
    
    private final CookieProperties cookieProperties;
    
    /**
     * Crea una cookie de access token y la agrega a la respuesta HTTP.
     * 
     * @param response Respuesta HTTP
     * @param accessToken Token JWT de acceso
     */
    public void createAccessTokenCookie(HttpServletResponse response, String accessToken) {
        log.debug("Creando cookie de access token");
        
        CookieProperties.CookieConfig config = cookieProperties.getAccessToken();
        Cookie cookie = createCookie(config.getName(), accessToken, config);
        
        response.addCookie(cookie);
    }
    
    /**
     * Crea una cookie de refresh token y la agrega a la respuesta HTTP.
     * 
     * @param response Respuesta HTTP
     * @param refreshToken Token JWT de refresh
     */
    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        log.debug("Creando cookie de refresh token");
        
        CookieProperties.CookieConfig config = cookieProperties.getRefreshToken();
        Cookie cookie = createCookie(config.getName(), refreshToken, config);
        
        response.addCookie(cookie);
    }
    
    /**
     * Crea una cookie con la configuración especificada.
     * 
     * @param name Nombre de la cookie
     * @param value Valor de la cookie
     * @param config Configuración de la cookie
     * @return Cookie creada
     */
    private Cookie createCookie(String name, String value, CookieProperties.CookieConfig config) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(config.isHttpOnly());
        cookie.setSecure(config.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge(config.getMaxAge());
        
        // Nota: SameSite no está disponible directamente en javax.servlet.http.Cookie
        // Se puede implementar mediante headers personalizados si es necesario
        
        return cookie;
    }
    
    /**
     * Obtiene el access token desde las cookies de la petición.
     * 
     * @param request Petición HTTP
     * @return Access token si está presente, vacío en caso contrario
     */
    public Optional<String> getAccessTokenFromCookies(HttpServletRequest request) {
        String cookieName = cookieProperties.getAccessToken().getName();
        return getCookieValue(request, cookieName);
    }
    
    /**
     * Obtiene el refresh token desde las cookies de la petición.
     * 
     * @param request Petición HTTP
     * @return Refresh token si está presente, vacío en caso contrario
     */
    public Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        String cookieName = cookieProperties.getRefreshToken().getName();
        return getCookieValue(request, cookieName);
    }
    
    /**
     * Obtiene el valor de una cookie específica.
     * 
     * @param request Petición HTTP
     * @param cookieName Nombre de la cookie
     * @return Valor de la cookie si está presente, vacío en caso contrario
     */
    private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.trim().isEmpty())
                .findFirst();
    }
    
    /**
     * Elimina las cookies de tokens de autenticación.
     * 
     * @param response Respuesta HTTP
     */
    public void clearAuthenticationCookies(HttpServletResponse response) {
        log.debug("Eliminando cookies de autenticación");
        
        clearCookie(response, cookieProperties.getAccessToken().getName());
        clearCookie(response, cookieProperties.getRefreshToken().getName());
    }
    
    /**
     * Elimina una cookie específica.
     * 
     * @param response Respuesta HTTP
     * @param cookieName Nombre de la cookie a eliminar
     */
    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        
        response.addCookie(cookie);
    }
    
    /**
     * Renueva las cookies de tokens con nuevos valores.
     * 
     * @param response Respuesta HTTP
     * @param newAccessToken Nuevo access token
     * @param newRefreshToken Nuevo refresh token (opcional)
     */
    public void renewTokenCookies(HttpServletResponse response, String newAccessToken, String newRefreshToken) {
        log.debug("Renovando cookies de tokens");
        
        createAccessTokenCookie(response, newAccessToken);
        
        if (newRefreshToken != null) {
            createRefreshTokenCookie(response, newRefreshToken);
        }
    }
}

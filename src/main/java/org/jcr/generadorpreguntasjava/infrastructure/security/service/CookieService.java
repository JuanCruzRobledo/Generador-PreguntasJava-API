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
     * @param response    Respuesta HTTP
     * @param accessToken Token JWT de acceso
     */
    public void createAccessTokenCookie(HttpServletResponse response, String accessToken) {
        log.debug("Creando cookie de access token");

        CookieProperties.CookieConfig config = cookieProperties.getAccessToken();
        String header = buildSetCookieHeader(config.getName(), accessToken, config);

        response.addHeader("Set-Cookie", header);
    }

    /**
     * Crea una cookie de refresh token y la agrega a la respuesta HTTP.
     *
     * @param response     Respuesta HTTP
     * @param refreshToken Token JWT de refresh
     */
    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        log.debug("Creando cookie de refresh token");

        CookieProperties.CookieConfig config = cookieProperties.getRefreshToken();
        String header = buildSetCookieHeader(config.getName(), refreshToken, config);

        response.addHeader("Set-Cookie", header);
    }

    /**
     * Construye manualmente un encabezado `Set-Cookie` compatible con SameSite, Secure, etc.
     *
     * @param name   Nombre de la cookie
     * @param value  Valor de la cookie
     * @param config Configuración de la cookie
     * @return String del header Set-Cookie correctamente formateado
     */
    private String buildSetCookieHeader(String name, String value, CookieProperties.CookieConfig config) {
        StringBuilder sb = new StringBuilder();

        sb.append(name).append("=").append(value).append("; ");
        sb.append("Path=/; ");
        sb.append("Max-Age=").append(config.getMaxAge()).append("; ");
        if (config.isHttpOnly()) sb.append("HttpOnly; ");
        if (config.isSecure()) sb.append("Secure; ");

        String sameSite = config.getSameSite();
        if (sameSite != null && !sameSite.isBlank()) {
            sb.append("SameSite=").append(sameSite);
        } else {
            sb.append("SameSite=Lax");
        }

        return sb.toString();
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
     * @param request    Petición HTTP
     * @param cookieName Nombre de la cookie
     * @return Valor de la cookie si está presente, vacío en caso contrario
     */
    private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

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

        clearCookie(response, cookieProperties.getAccessToken());
        clearCookie(response, cookieProperties.getRefreshToken());
    }

    /**
     * Elimina una cookie específica generando un Set-Cookie con Max-Age=0.
     *
     * @param response Respuesta HTTP
     * @param config   Configuración de la cookie a eliminar
     */
    private void clearCookie(HttpServletResponse response, CookieProperties.CookieConfig config) {
        String header = buildSetCookieHeader(config.getName(), "", new CookieProperties.CookieConfig() {{
            setName(config.getName());
            setHttpOnly(config.isHttpOnly());
            setSecure(config.isSecure());
            setSameSite(config.getSameSite());
            setMaxAge(0); // Eliminar cookie
        }});
        response.addHeader("Set-Cookie", header);
    }

    /**
     * Renueva las cookies de tokens con nuevos valores.
     *
     * @param response        Respuesta HTTP
     * @param newAccessToken  Nuevo access token
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


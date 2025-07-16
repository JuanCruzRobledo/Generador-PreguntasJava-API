package org.jcr.generadorpreguntasjava.infrastructure.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de propiedades de cookies.
 * Lee las configuraciones desde application.properties.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security.cookies")
public class CookieProperties {
    
    /**
     * Configuración de cookie para access token.
     */
    private CookieConfig accessToken = new CookieConfig();
    
    /**
     * Configuración de cookie para refresh token.
     */
    private CookieConfig refreshToken = new CookieConfig();
    
    /**
     * Configuración de una cookie específica.
     */
    @Data
    public static class CookieConfig {
        /**
         * Nombre de la cookie.
         */
        private String name;
        
        /**
         * Si la cookie debe ser secure (HTTPS).
         */
        private boolean secure;
        
        /**
         * Si la cookie debe ser HttpOnly.
         */
        private boolean httpOnly;
        
        /**
         * Configuración SameSite de la cookie.
         */
        private String sameSite;
        
        /**
         * Tiempo de vida máximo de la cookie en segundos.
         */
        private int maxAge;
    }
}

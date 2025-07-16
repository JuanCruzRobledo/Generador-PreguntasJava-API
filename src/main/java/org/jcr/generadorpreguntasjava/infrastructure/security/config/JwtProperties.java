package org.jcr.generadorpreguntasjava.infrastructure.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de propiedades JWT.
 * Lee las configuraciones desde application.properties.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    
    /**
     * Secreto para firmar los tokens JWT.
     */
    private String secret;
    
    /**
     * Configuración del access token.
     */
    private TokenConfig accessToken = new TokenConfig();
    
    /**
     * Configuración del refresh token.
     */
    private TokenConfig refreshToken = new TokenConfig();
    
    /**
     * Configuración de un token específico.
     */
    @Data
    public static class TokenConfig {
        /**
         * Tiempo de expiración en milisegundos.
         */
        private long expiration;
    }
}

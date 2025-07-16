package org.jcr.generadorpreguntasjava.infrastructure.security.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para habilitar las propiedades de configuración de seguridad.
 * Permite que Spring Boot cargue las propiedades desde application.properties.
 */
@Configuration
@EnableConfigurationProperties({
    JwtProperties.class,
    CookieProperties.class,
    CsrfProperties.class
})
public class SecurityPropertiesConfig {
}

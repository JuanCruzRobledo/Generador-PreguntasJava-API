package org.jcr.generadorpreguntasjava.infrastructure.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.csrf")
public class CsrfProperties {

    private boolean enabled = true;
    private String tokenRepository = "cookie";
    private CookieConfig cookie = new CookieConfig();

    @Data
    public static class CookieConfig {
        private String name = "XSRF-TOKEN";
        private boolean secure = false;
        private boolean httpOnly = false;
        private String sameSite = "lax";
    }
}
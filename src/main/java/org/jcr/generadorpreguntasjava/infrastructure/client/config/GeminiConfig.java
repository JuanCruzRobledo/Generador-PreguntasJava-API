package org.jcr.generadorpreguntasjava.infrastructure.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración para el cliente HTTP de Gemini.
 */
@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Data
public class GeminiConfig {
    
    private String key;
    private String url;
    private String model;
    private Integer maxTokens;
    private Double temperature;
    
    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
            .baseUrl(url)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}

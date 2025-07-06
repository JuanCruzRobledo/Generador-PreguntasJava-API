package org.jcr.generadorpreguntasjava.infrastructure.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración para el cliente HTTP de OpenAI.
 */
@Configuration
@ConfigurationProperties(prefix = "openai.api")
@Data
public class OpenAiConfig {
    
    private String key;
    private String url;
    private String model;
    private Integer maxTokens;
    private Double temperature;
    
    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
            .baseUrl(url)
            .defaultHeader("Authorization", "Bearer " + key)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}

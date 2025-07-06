package org.jcr.generadorpreguntasjava.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTOs para las peticiones a OpenAI.
 */
public class OpenAiRequest {
    
    public record ChatCompletionRequest(
        String model,
        List<Message> messages,
        
        @JsonProperty("max_tokens")
        Integer maxTokens,
        
        Double temperature
    ) {}
    
    public record Message(
        String role,
        String content
    ) {}
}

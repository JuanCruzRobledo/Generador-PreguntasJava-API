package org.jcr.generadorpreguntasjava.infrastructure.client.dto;

import java.util.List;

/**
 * DTOs para las peticiones a Gemini.
 */
public class GeminiRequest {
    
    public record GenerateContentRequest(
        List<Content> contents,
        GenerationConfig generationConfig
    ) {}
    
    public record Content(
        List<Part> parts
    ) {}
    
    public record Part(
        String text
    ) {}
    
    public record GenerationConfig(
        Integer maxOutputTokens,
        Double temperature
    ) {}
}

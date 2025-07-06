package org.jcr.generadorpreguntasjava.infrastructure.client.dto;

import java.util.List;

/**
 * DTOs para las respuestas de Gemini.
 */
public class GeminiResponse {
    
    public record GenerateContentResponse(
        List<Candidate> candidates,
        UsageMetadata usageMetadata
    ) {}
    
    public record Candidate(
        Content content,
        String finishReason,
        Integer index
    ) {}
    
    public record Content(
        List<Part> parts,
        String role
    ) {}
    
    public record Part(
        String text
    ) {}
    
    public record UsageMetadata(
        Integer promptTokenCount,
        Integer candidatesTokenCount,
        Integer totalTokenCount
    ) {}
}

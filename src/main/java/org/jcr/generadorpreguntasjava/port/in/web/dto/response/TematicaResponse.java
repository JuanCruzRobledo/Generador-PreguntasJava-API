package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para temáticas.
 */
public record TematicaResponse(
    Long id,
    String nombre,
    
    @JsonProperty("contadorUsos")
    Integer contadorUsos,
    
    @JsonProperty("timestampUltimoUso")
    LocalDateTime timestampUltimoUso
) {}

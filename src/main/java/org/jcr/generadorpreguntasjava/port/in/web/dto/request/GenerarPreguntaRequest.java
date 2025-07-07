package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de request para generar preguntas.
 */
public record GenerarPreguntaRequest(
    String dificultad,
    
    @JsonProperty("tematicaDeseada")
    String tematicaDeseada
) {}

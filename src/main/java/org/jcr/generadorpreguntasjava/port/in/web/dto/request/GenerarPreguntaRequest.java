package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO de request para generar preguntas.
 */
public record GenerarPreguntaRequest(
    String dificultad,
    
    @JsonProperty("tematicasDeseadas")
    List<String> tematicasDeseadas,
    @JsonProperty("tematicasYaUtilizadas")
    List<String> tematicasYaUtilizadas
) {}

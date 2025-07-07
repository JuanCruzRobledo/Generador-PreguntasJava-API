package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para estadísticas por temática.
 */
public record EstadisticasTematicaResponse(
    @JsonProperty("tematica")
    String tematica,
    
    @JsonProperty("totalPreguntas")
    int totalPreguntas,
    
    @JsonProperty("respuestasCorrectas")
    int respuestasCorrectas,
    
    @JsonProperty("porcentajeAciertos")
    double porcentajeAciertos,
    
    @JsonProperty("tiempoPromedio")
    String tiempoPromedio,
    
    @JsonProperty("esBuenRendimiento")
    boolean esBuenRendimiento,
    
    @JsonProperty("esTematicaFavorita")
    boolean esTematicaFavorita
) {}

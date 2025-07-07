package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para estadísticas por dificultad.
 */
public record EstadisticasDificultadResponse(
    @JsonProperty("dificultad")
    String dificultad,
    
    @JsonProperty("totalPreguntas")
    int totalPreguntas,
    
    @JsonProperty("respuestasCorrectas")
    int respuestasCorrectas,
    
    @JsonProperty("porcentajeAciertos")
    double porcentajeAciertos,
    
    @JsonProperty("tiempoPromedio")
    String tiempoPromedio,
    
    @JsonProperty("esBuenRendimiento")
    boolean esBuenRendimiento
) {}

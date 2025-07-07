package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * DTO de respuesta para estadísticas de usuario.
 */
public record EstadisticasResponse(
    @JsonProperty("totalPreguntas")
    int totalPreguntas,
    
    @JsonProperty("respuestasCorrectas")
    int respuestasCorrectas,
    
    @JsonProperty("porcentajeAciertos")
    double porcentajeAciertos,
    
    @JsonProperty("tiempoPromedio")
    String tiempoPromedio,
    
    @JsonProperty("porDificultad")
    Map<String, EstadisticasDificultadResponse> porDificultad,
    
    @JsonProperty("porTematica")
    Map<String, EstadisticasTematicaResponse> porTematica,
    
    @JsonProperty("nivelUsuario")
    String nivelUsuario,
    
    @JsonProperty("tieneBuenRendimiento")
    boolean tieneBuenRendimiento
) {}

package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO de respuesta para preguntas.
 */
public record PreguntaResponse(
    Long id,
    
    @JsonProperty("codigoJava")
    String codigoJava,
    
    String enunciado,
    
    String dificultad,

    String explicacion,
    
    List<OpcionResponse> opciones,
    
    List<TematicaResponse> tematicas
) {}

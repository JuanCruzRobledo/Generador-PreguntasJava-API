package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO de respuesta para preguntas.
 */
public record PreguntaResponse(
    Long id,
    
    @JsonProperty("codigo_java")
    String codigoJava,
    
    String enunciado,
    
    String dificultad,
    
    //@JsonProperty("respuesta_correcta")
    //String respuestaCorrecta,
    
    String explicacion,
    
    List<OpcionResponse> opciones,
    
    List<TematicaResponse> tematicas
) {}

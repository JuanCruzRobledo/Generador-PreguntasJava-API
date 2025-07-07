package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para validación de respuestas.
 */
public record ValidacionResponse(
    @JsonProperty("esCorrecta")
    boolean esCorrecta,
    
    String explicacion,
    
    @JsonProperty("respuestaCorrecta")
    String respuestaCorrecta
) {}

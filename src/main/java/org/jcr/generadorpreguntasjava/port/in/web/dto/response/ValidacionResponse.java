package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

/**
 * DTO de respuesta para validación de respuestas.
 */
public record ValidacionResponse(
    boolean esCorrecta,
    String explicacion,
    String respuestaCorrecta
) {}

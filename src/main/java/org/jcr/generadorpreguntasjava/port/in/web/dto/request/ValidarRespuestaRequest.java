package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de request para validar respuestas.
 */
public record ValidarRespuestaRequest(
    @NotNull
    Long preguntaId,
    
    @NotNull
    String opcionSeleccionada
) {}

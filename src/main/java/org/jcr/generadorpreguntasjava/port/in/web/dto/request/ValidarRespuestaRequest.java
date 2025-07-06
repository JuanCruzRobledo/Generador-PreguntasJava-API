package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de request para validar respuestas.
 */
public record ValidarRespuestaRequest(
    @NotNull
    @JsonProperty("pregunta_id")
    Long preguntaId,
    
    @NotNull
    @JsonProperty("opcion_seleccionada")
    String opcionSeleccionada
) {}

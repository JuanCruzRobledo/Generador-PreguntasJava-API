package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para temáticas.
 */
public record TematicaResponse(
    Long id,
    String nombre,
    Integer contadorUsos,
    LocalDateTime timestampUltimoUso
) {}

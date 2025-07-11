package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import java.util.List;

/**
 * DTO de respuesta para preguntas.
 */
public record PreguntaResponse(
    Long id,
    String codigoFuente,
    String enunciado,
    String dificultad,
    String explicacion,
    List<OpcionResponse> opciones,
    List<TematicaResponse> tagsTematicas
) {}

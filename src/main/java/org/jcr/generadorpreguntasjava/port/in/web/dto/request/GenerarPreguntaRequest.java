package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import java.util.List;

/**
 * DTO de request para generar preguntas.
 */
public record GenerarPreguntaRequest(
    String dificultad,
    String lenguaje,
    List<String> tagsTematicas,
    List<String> tagsYaUtilizados,
    String categoriaPrincipal
) {}

package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

/**
 * DTO de respuesta para estadísticas por temática.
 */
public record EstadisticasTematicaResponse(
    String tematica,
    int totalPreguntas,
    int respuestasCorrectas,
    double porcentajeAciertos,
    String tiempoPromedio,
    boolean esBuenRendimiento,
    boolean esTematicaFavorita
) {}

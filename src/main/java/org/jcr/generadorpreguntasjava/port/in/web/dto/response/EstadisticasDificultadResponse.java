package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

/**
 * DTO de respuesta para estadísticas por dificultad.
 */
public record EstadisticasDificultadResponse(
    String dificultad,
    int totalPreguntas,
    int respuestasCorrectas,
    double porcentajeAciertos,
    String tiempoPromedio,
    boolean esBuenRendimiento
) {}

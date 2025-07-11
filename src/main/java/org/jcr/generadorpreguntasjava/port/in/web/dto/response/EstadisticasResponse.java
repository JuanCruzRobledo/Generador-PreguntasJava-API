package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import java.util.Map;

/**
 * DTO de respuesta para estadísticas de usuario.
 */
public record EstadisticasResponse(
    int totalPreguntas,
    int respuestasCorrectas,
    double porcentajeAciertos,
    String tiempoPromedio,
    Map<String, EstadisticasDificultadResponse> porDificultad,
    Map<String, EstadisticasTematicaResponse> porTematica,
    String nivelUsuario,
    boolean tieneBuenRendimiento
) {}

package org.jcr.generadorpreguntasjava.port.in.web.dto.request;

import org.jcr.generadorpreguntasjava.domain.model.Dificultad;

import java.util.List;

public record GenerarExamenRequest(
        Dificultad dificultad,
        int cantidadPreguntas,
        List<String> tematicasDeseadas,
        List<String> tematicasExcluidas,
        String titulo,
        String descripcion
) {}

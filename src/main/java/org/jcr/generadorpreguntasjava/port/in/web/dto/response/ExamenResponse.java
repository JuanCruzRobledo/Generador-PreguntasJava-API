package org.jcr.generadorpreguntasjava.port.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ExamenResponse(
        Long id,
        String titulo,
        String descripcion,
        String dificultadPromedio,
        LocalDateTime fechaCreacion,
        List<TematicaResponse> tematicas,
        List<PreguntaResponse> preguntas
) {}

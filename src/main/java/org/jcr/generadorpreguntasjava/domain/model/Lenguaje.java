package org.jcr.generadorpreguntasjava.domain.model;

import java.util.List;

public record Lenguaje(
        Long id,
        String nombre,
        String descripcion,
        List<Pregunta> preguntas,
        List<CategoriaTematica> categoriasPrincipales
) {
}

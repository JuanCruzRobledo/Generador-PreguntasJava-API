package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.Examen;

import java.util.List;

/**
 * Puerto de entrada para generar exámenes.
 * Define el contrato para el caso de uso de generación de exámenes completos.
 */
public interface GenerarExamenPort {

    /**
     * Genera un nuevo examen con las características especificadas.
     *
     * @param titulo Título descriptivo del examen
     * @param descripcion Descripción detallada del examen (opcional)
     * @param dificultad Dificultad objetivo del examen
     * @param cantidadPreguntas Número de preguntas que debe contener el examen
     * @param tematicasDeseadas Lista de temáticas sobre las cuales generar preguntas
     * @param tematicasExcluidas Lista de temáticas que deben evitarse
     * @return Examen generado y persistido
     * @throws IllegalArgumentException si los parámetros no son válidos
     * @throws RuntimeException si hay errores en la generación o persistencia
     */
    Examen generarExamen(
            String titulo,
            String descripcion,
            Dificultad dificultad,
            int cantidadPreguntas,
            List<String> tematicasDeseadas,
            List<String> tematicasExcluidas
    );
}
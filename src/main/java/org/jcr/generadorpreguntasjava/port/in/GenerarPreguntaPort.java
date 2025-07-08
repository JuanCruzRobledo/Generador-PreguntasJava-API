package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.Pregunta;

import java.util.List;

/**
 * Puerto de entrada para generar preguntas.
 * Define el contrato para el caso de uso de generación de preguntas.
 */
public interface GenerarPreguntaPort {
    
    /**
     * Genera una nueva pregunta con los parámetros especificados.
     * 
     * @param dificultad Nivel de dificultad deseado (opcional)
     * @param tematicasDeseadas     Lista de temáticas sobre las cuales generar preguntas
     * @param tematicasYaUtilizadas Lista de temáticas que ya se usaron y deben evitarse
     * @return Pregunta generada y persistida
     * @throws IllegalArgumentException si los parámetros no son válidos
     * @throws RuntimeException si hay errores en la generación o persistencia
     */
    Pregunta generarPregunta(Dificultad dificultad, List<String> tematicasDeseadas , List<String> tematicasYaUtilizadas);
}

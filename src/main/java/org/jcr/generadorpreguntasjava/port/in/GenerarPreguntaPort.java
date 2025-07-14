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
     * @param lenguajeId Id del Lenguaje de programacion (opcional)
     * @param categoriaId  Id de la Categoria la cual se van a tratar las preguntas (POO, Estructural)
     * @param tagsTematicas     Lista de temáticas sobre las cuales generar preguntas
     * @param tagsYaUtilizados Lista de temáticas que ya se usaron y deben evitarse
     * @return Pregunta generada y persistida
     * @throws IllegalArgumentException si los parámetros no son válidos
     * @throws RuntimeException si hay errores en la generación o persistencia
     */
    Pregunta generarPregunta(Dificultad dificultad,Long lenguajeId, Long categoriaId, List<String> tagsTematicas , List<String> tagsYaUtilizados);
}

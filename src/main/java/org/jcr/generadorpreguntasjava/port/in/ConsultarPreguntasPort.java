package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.Pregunta;
import org.jcr.generadorpreguntasjava.domain.model.Tematica;

import java.util.List;

/**
 * Puerto de entrada para consultar preguntas.
 * Define el contrato para el caso de uso de consulta de preguntas.
 */
public interface ConsultarPreguntasPort {
    
    /**
     * Obtiene todas las preguntas disponibles.
     * 
     * @return Lista de todas las preguntas
     */
    List<Pregunta> obtenerTodasLasPreguntas();
    
    /**
     * Obtiene las preguntas asociadas a una temática específica.
     * 
     * @param nombreTematica Nombre de la temática
     * @return Lista de preguntas de la temática especificada
     * @throws IllegalArgumentException si el nombre de la temática no es válido
     */
    List<Pregunta> obtenerPreguntasPorTematica(String nombreTematica);
    
    /**
     * Obtiene todas las temáticas disponibles con su información de uso.
     * 
     * @return Lista de todas las temáticas
     */
    List<Tematica> obtenerTodasLasTematicas();
}

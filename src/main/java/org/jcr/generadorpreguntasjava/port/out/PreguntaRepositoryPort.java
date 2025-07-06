package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de preguntas.
 * Define el contrato para operaciones de persistencia de preguntas.
 */
public interface PreguntaRepositoryPort {
    
    /**
     * Guarda una pregunta en el repositorio.
     * 
     * @param pregunta Pregunta a guardar
     * @return Pregunta guardada con ID asignado
     */
    Pregunta guardar(Pregunta pregunta);
    
    /**
     * Busca una pregunta por su ID.
     * 
     * @param id ID de la pregunta
     * @return Optional con la pregunta si existe
     */
    Optional<Pregunta> buscarPorId(Long id);
    
    /**
     * Obtiene todas las preguntas.
     * 
     * @return Lista de todas las preguntas
     */
    List<Pregunta> obtenerTodas();
    
    /**
     * Busca preguntas por temática.
     * 
     * @param nombreTematica Nombre normalizado de la temática
     * @return Lista de preguntas que contienen la temática
     */
    List<Pregunta> buscarPorTematica(String nombreTematica);
    
    /**
     * Verifica si existe al menos una pregunta en el repositorio.
     * 
     * @return true si existe al menos una pregunta
     */
    boolean existeAlguna();
}

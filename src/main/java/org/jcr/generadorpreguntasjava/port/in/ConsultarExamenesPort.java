package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.Examen;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para consultar exámenes.
 * Define el contrato para los casos de uso de consulta de exámenes.
 */
public interface ConsultarExamenesPort {

    /**
     * Obtiene todos los exámenes disponibles en el sistema.
     *
     * @return Lista de todos los exámenes ordenados por fecha de creación descendente
     * @throws RuntimeException si hay errores en la consulta
     */
    List<Examen> obtenerTodosLosExamenes();

    /**
     * Busca un examen por su identificador único.
     *
     * @param id Identificador único del examen
     * @return Examen encontrado envuelto en un Optional
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si hay errores en la consulta
     */
    Optional<Examen> obtenerExamenPorId(Long id);

    /**
     * Obtiene exámenes que contengan preguntas de la temática especificada.
     *
     * @param nombreTematica Nombre de la temática a buscar
     * @return Lista de exámenes que contienen la temática, ordenados por fecha
     * @throws IllegalArgumentException si el nombre de temática es inválido
     * @throws RuntimeException si hay errores en la consulta
     */
    List<Examen> obtenerExamenesPorTematica(String nombreTematica);

    /**
     * Obtiene exámenes filtrados por rango de fechas.
     *
     * @param desde Fecha de inicio (inclusive)
     * @param hasta Fecha de fin (inclusive)
     * @return Lista de exámenes creados en el rango especificado
     * @throws IllegalArgumentException si las fechas son inválidas
     * @throws RuntimeException si hay errores en la consulta
     */
    List<Examen> obtenerExamenesPorFecha(LocalDateTime desde, LocalDateTime hasta);
}
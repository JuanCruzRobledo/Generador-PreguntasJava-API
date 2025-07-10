package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Tematica;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de temáticas.
 * Define el contrato para operaciones de persistencia de temáticas.
 */
public interface TematicaRepositoryPort {
    
    /**
     * Guarda una temática en el repositorio.
     * 
     * @param tematica Temática a guardar
     * @return Temática guardada con ID asignado
     */
    Tematica guardar(Tematica tematica);
    
    /**
     * Busca una temática por su nombre.
     * 
     * @param nombre Nombre normalizado de la temática
     * @return Optional con la temática si existe
     */
    Optional<Tematica> buscarPorNombre(String nombre);
    
    /**
     * Obtiene todas las temáticas.
     * 
     * @return Lista de todas las temáticas
     */
    List<Tematica> obtenerTodas();

    Tematica persistirConIntegridad(Tematica tematica);
    
}

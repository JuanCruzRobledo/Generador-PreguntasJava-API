package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.TagTematica;

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
     * @param tagTematica Temática a guardar
     * @return Temática guardada con ID asignado
     */
    TagTematica guardar(TagTematica tagTematica);
    
    /**
     * Busca una temática por su nombre.
     * 
     * @param nombre Nombre normalizado de la temática
     * @return Optional con la temática si existe
     */
    Optional<TagTematica> buscarPorNombre(String nombre);
    
    /**
     * Obtiene todas las temáticas.
     * 
     * @return Lista de todas las temáticas
     */
    List<TagTematica> obtenerTodas();

    /**
     * Obtiene todas las temáticas.
     *
     * @return Lista de todas las temáticas
     */
    List<TagTematica> obtenerTodosDeCategoria(Long id);

    TagTematica persistirConIntegridad(TagTematica tagTematica);
    
}

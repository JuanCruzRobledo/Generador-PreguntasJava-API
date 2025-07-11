package org.jcr.generadorpreguntasjava.port.out;


import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;

/**
 * COMENTARIO
 *
 */
public interface CategoriaTematicaRepositoryPort {

    /**
     * Guarda el Examen en el repositorio.
     *
     * @param categoriaTematica Categoria a guardar
     * @return Categoria guardada con ID asignado
     */
    CategoriaTematica guardar(CategoriaTematica categoriaTematica);
}

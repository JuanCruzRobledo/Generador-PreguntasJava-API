package org.jcr.generadorpreguntasjava.port.in;

import org.jcr.generadorpreguntasjava.domain.model.TagTematica;

import java.util.List;

public interface ConsultarTagTematicaPort {
    /**
     * Obtiene todos los tags disponibles con su información de uso.
     *
     * @return Lista de todas los tags
     */
    List<TagTematica> obtenerTodasLasTematicas();

    /**
     * Obtiene todos los tags de una categoria principal disponibles con su información de uso.
     *
     * @param id  Id de la categoria principal
     * @return Lista de todas los tags de una categoria principal
     */
    List<TagTematica> obtenerTodasLasTematicas(Long id);
}

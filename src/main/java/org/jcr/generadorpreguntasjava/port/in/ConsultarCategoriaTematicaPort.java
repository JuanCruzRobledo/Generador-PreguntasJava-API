package org.jcr.generadorpreguntasjava.port.in;


import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;

import java.util.List;

/**
 * Puerto de entrada para consultar Categorias principales.
 * Define el contrato para el caso de uso de consulta de preguntas.
 */
public interface ConsultarCategoriaTematicaPort {
    /**
     * Obtiene todas las preguntas disponibles.
     *
     * @return Lista de todas las Categorias principales
     */
    List<CategoriaTematica> obtenerTodasLasCategorias();

    List<CategoriaTematica> obtenerTodasLasCategoriasDeUnLenguaje(Long id);
}

package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;

import java.util.List;

/**
 * Puerto de salida para persistencia de Lenguajes.
 * Define el contrato para operaciones de persistencia de Lenguajes.
 */
public interface LenguajeRepositoryPort {

    /**
     * Guarda un lenguaje en el repositorio.
     *
     * @param pregunta Lenguaje a guardar
     * @return Lenguaje guardado con ID asignado
     */
    Lenguaje guardar(Lenguaje pregunta);

    List<Lenguaje> obtenerLenguajes();
}

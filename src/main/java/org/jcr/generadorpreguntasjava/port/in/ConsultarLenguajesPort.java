package org.jcr.generadorpreguntasjava.port.in;


import org.jcr.generadorpreguntasjava.domain.model.Lenguaje;
import java.util.List;

/**
 * Puerto de entrada para consultar lenguajes.
 * Define el contrato para el caso de uso de consulta de lenguajes.
 */
public interface ConsultarLenguajesPort {

    /**
     * Obtiene todos los lenguajes disponibles.
     *
     * @return Lista de todas los lenguajes
     */
    List<Lenguaje> obtenerTodosLosLenguajes();

}

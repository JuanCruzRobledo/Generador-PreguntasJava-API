package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Examen;

public interface ExamenRepositoryPort {
    /**
     * Guarda el Examen en el repositorio.
     *
     * @param examen Examen a guardar
     * @return Pregunta guardada con ID asignado
     */
    Examen guardar(Examen examen);
}

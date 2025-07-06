package org.jcr.generadorpreguntasjava.port.in;

/**
 * Puerto de entrada para validar respuestas.
 * Define el contrato para el caso de uso de validación de respuestas.
 */
public interface ValidarRespuestaPort {
    
    /**
     * Resultado de validación de una respuesta.
     */
    record ResultadoValidacion(
        boolean esCorrecta,
        String explicacion,
        String respuestaCorrecta
    ) {}
    
    /**
     * Valida si una respuesta proporcionada es correcta para una pregunta específica.
     * 
     * @param preguntaId ID de la pregunta
     * @param respuestaDada Respuesta proporcionada por el usuario
     * @return Resultado de la validación incluyendo explicación
     * @throws IllegalArgumentException si los parámetros no son válidos
     * @throws RuntimeException si la pregunta no existe
     */
    ResultadoValidacion validarRespuesta(Long preguntaId, String respuestaDada);
}

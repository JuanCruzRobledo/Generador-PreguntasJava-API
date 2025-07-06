package org.jcr.generadorpreguntasjava.port.out;

import org.jcr.generadorpreguntasjava.domain.model.Dificultad;

/**
 * Puerto de salida para el servicio de generación de preguntas.
 * Define el contrato para generar preguntas usando servicios externos (ej: OpenAI).
 */
public interface GeneradorDePreguntaServicePort {
    
    /**
     * Respuesta del servicio de generación de preguntas.
     */
    record RespuestaGeneracion(
        String codigoJava,
        String enunciado,
        String[] opciones,
        String respuestaCorrecta,
        String explicacion,
        String tematicaPrincipal,
        String tematicaSecundaria,
        String dificultad
    ) {}
    
    /**
     * Genera una pregunta usando un prompt personalizado.
     * 
     * @param prompt Prompt completo para la generación
     * @return Respuesta del servicio con todos los datos de la pregunta
     * @throws RuntimeException si hay errores en la comunicación con el servicio
     */
    RespuestaGeneracion generarPregunta(String prompt);
}

package org.jcr.generadorpreguntasjava.domain.service;

/**
 * Plantilla base para la generación de prompts dinámicos.
 * 
 * Clase del dominio que encapsula la lógica de construcción de prompts.
 */
public class PromptTemplate {
    
    public static final String BASE_PROMPT = """
        Eres un experto en programación Java y educación. Tu tarea es generar una pregunta de opción múltiple sobre código Java secuencial.
        
        INSTRUCCIONES IMPORTANTES:
        1. El código debe ser autocontenido y ejecutable
        2. Debe ser sobre Java secuencial (sin hilos, async, etc.)
        3. La pregunta debe tener exactamente 4 opciones de respuesta
        4. Solo una opción debe ser correcta
        5. Incluye una explicación clara de por qué la respuesta es correcta
        6. Las temáticas principales y secundarias deben ser conceptos específicos de Java
        
        DIFICULTAD SOLICITADA: {dificultad}
        
        TEMÁTICA PREFERIDA: {tematicaDeseada}
        
        TEMÁTICAS YA UTILIZADAS (NO REPETIR):
        {tematicasUsadas}
        
        FORMATO DE RESPUESTA REQUERIDO (JSON válido):
        {
          "codigoJava": "código Java completo y ejecutable",
          "enunciado": "pregunta clara sobre qué hace el código",
          "opciones": [
            "Opción A",
            "Opción B", 
            "Opción C",
            "Opción D"
          ],
          "respuestaCorrecta": "Opción correcta exacta",
          "explicacion": "explicación detallada de por qué es correcta",
          "tematicaPrincipal": "concepto principal (ej: arrays, bucles, etc.)",
          "tematicaSecundaria": "concepto secundario (ej: indexación, condiciones, etc.)",
          "dificultad": "facil|media|dificil"
        }
        
        IMPORTANTE: Responde ÚNICAMENTE con el JSON válido, sin texto adicional.
        """;

    public static final String EXAMEN_PROMPT = """
        Eres un experto en programación Java y educación. Tu tarea es generar un examen completo con {cantidadPreguntas} preguntas sobre Java.
        
        REQUISITOS:
        1. Cada pregunta debe seguir el formato estándar (código, 4 opciones, explicación)
        2. Variedad de temáticas: {tematicaDeseada}
        3. Evitar: {tematicasExcluidas}
        4. Dificultad general: {dificultad}
        5. Distribuir temáticas equilibradamente
        6. Incluir preguntas conceptuales y de código
        
        FORMATO DE RESPUESTA (JSON válido):
        {
          "titulo": "Título sugerido para el examen",
          "descripcion": "Descripción breve del examen",
          "preguntas": [
            {
              "codigoJava": "...",
              "enunciado": "...",
              "opciones": ["...", "...", "...", "..."],
              "respuestaCorrecta": "...",
              "explicacion": "...",
              "tematicaPrincipal": "...",
              "tematicaSecundaria": "...",
              "dificultad": "..."
            }
            // ... repetir para cada pregunta
          ]
        }
        
        Responde ÚNICAMENTE con el JSON válido, sin texto adicional.
        """;

    public static String construirPromptExamen(String dificultad, int cantidadPreguntas,
                                               String tematicaDeseada, String tematicasExcluidas) {
        return EXAMEN_PROMPT
                .replace("{dificultad}", dificultad)
                .replace("{cantidadPreguntas}", String.valueOf(cantidadPreguntas))
                .replace("{tematicaDeseada}", tematicaDeseada)
                .replace("{tematicasExcluidas}", tematicasExcluidas);
    }
    
    public static String construirPrompt(String dificultad, String tematicaDeseada, String tematicasUsadas) {
        return BASE_PROMPT
            .replace("{dificultad}", dificultad != null ? dificultad : "cualquiera")
            .replace("{tematicaDeseada}", tematicaDeseada != null ? tematicaDeseada : "cualquiera")
            .replace("{tematicasUsadas}", tematicasUsadas != null ? tematicasUsadas : "Ninguna");
    }
}

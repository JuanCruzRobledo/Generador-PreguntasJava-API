package org.jcr.generadorpreguntasjava.domain.service;

/**
 * Plantilla base para la generación de prompts dinámicos.
 * 
 * Clase del dominio que encapsula la lógica de construcción de prompts.
 */
public class PromptTemplate {

    public static final String BASE_PROMPT = """
        Eres un experto en enseñanza de programación en {lenguaje}. Tu tarea es generar una **pregunta de opción múltiple** basada en código, clara, técnica y alineada con objetivos educativos.
        
        🧠 OBJETIVO:
        Generar una pregunta sobre código en {lenguaje} con un enfoque didáctico, según la dificultad y categoría solicitada.
        
        📌 REQUISITOS:
        1. El código debe ser autocontenido, ejecutable y representar correctamente el concepto central.
        2. No utilices hilos, concurrencia ni estructuras avanzadas (a menos que lo pida la categoría).
        3. La pregunta debe tener exactamente 4 opciones, solo UNA debe ser correcta.
        4. La explicación debe ser clara, precisa y justificar la opción correcta en relación con el código.
        5. La **categoría principal** es un eje temático amplio del lenguaje (por ejemplo: Fundamentos de Java, Programación Orientada a Objetos, Excepciones).
        6. Los **tagsTematicas** deben representar los **conceptos específicos tratados por el ejercicio**, por ejemplo:
           - Si la categoría es *Fundamentos de Java*: `suma de variables`, `concatenación`, `operadores lógicos`
           - Si la categoría es *POO*: `herencia`, `polimorfismo`, `encapsulamiento`
        7. **Evita repetir temas ya utilizados**, indicados en la sección "Temáticas ya utilizadas".
        
        🎯 PARÁMETROS DE ENTRADA:
        - Lenguaje: {lenguaje}
        - Dificultad: {dificultad} (fácil | media | difícil)
        - Categoría principal: {categoriaPrincipal}
        - Temáticas específicas deseadas: {tagsTematicas}
        - Temáticas ya utilizadas (NO repetir): {tematicasUsadas}
        
        📦 FORMATO DE RESPUESTA (JSON válido):
        {
          "codigoFuente": "Bloque de código {lenguaje}, completo y ejecutable",
          "enunciado": "Pregunta clara sobre qué hace el código o cuál es el resultado",
          "opciones": [
            "Opción A",
            "Opción B",
            "Opción C",
            "Opción D"
          ],
          "respuestaCorrecta": "Texto exacto de la opción correcta",
          "explicacion": "Explicación detallada y didáctica de por qué esa opción es correcta",
          "categoriaPrincipal": "Ej: Fundamentos de Java, POO, Excepciones",
          "tagsTematicas": [
            "suma de variables",
            "operadores lógicos",
            "concatenación"
          ],
          "dificultad": "facil | media | dificil"
        }
        
        🚫 IMPORTANTE: Responde ÚNICAMENTE con el JSON válido, sin texto adicional ni comentarios.
        """;

    public static final String EXAMEN_PROMPT = """
        Eres un experto en programación {lenguaje} y educación. Tu tarea es generar un examen completo con {cantidadPreguntas} preguntas sobre {lenguaje}.
        
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
              "tagTematicaPrincipal": "...",
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

    public static String construirPrompt(String dificultad, String lenguaje, String categoriaPrincipal, String tagsDeseadosStr, String tagsUsadosStr) {
        return BASE_PROMPT
                .replace("{dificultad}", isNullOrEmpty(dificultad) ? "cualquiera" : dificultad)
                .replace("{categoriaPrincipal}", isNullOrEmpty(categoriaPrincipal) ? "Fundamentos de Java" : categoriaPrincipal)
                .replace("{lenguaje}", isNullOrEmpty(lenguaje) ? "Java" : lenguaje)
                .replace("{tagsTematicas}", isNullOrEmpty(tagsDeseadosStr) ? "cualquier temática relacionada a " + (isNullOrEmpty(categoriaPrincipal) ? "Fundamentos de Java" : categoriaPrincipal) : tagsDeseadosStr)
                .replace("{tematicasUsadas}", isNullOrEmpty(tagsUsadosStr) ? "ninguna temática aún utilizada" : tagsUsadosStr);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}

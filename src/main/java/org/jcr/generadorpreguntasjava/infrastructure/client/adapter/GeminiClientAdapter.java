package org.jcr.generadorpreguntasjava.infrastructure.client.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.infrastructure.client.config.GeminiConfig;
import org.jcr.generadorpreguntasjava.infrastructure.client.dto.GeminiRequest;
import org.jcr.generadorpreguntasjava.infrastructure.client.dto.GeminiResponse;
import org.jcr.generadorpreguntasjava.port.out.GeneradorDePreguntaServicePort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador secundario que implementa el puerto de salida para generar preguntas usando Gemini.
 * 
 * Se conecta con la API de Gemini para generar preguntas de código Java.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class GeminiClientAdapter implements GeneradorDePreguntaServicePort {
    
    private final WebClient geminiWebClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;
    
    @Override
    public RespuestaGeneracion generarPregunta(String prompt) {
        log.info("Enviando prompt a Gemini para generar pregunta");
        
        try {
            // Si la API key es de prueba, simular respuesta
            if (geminiConfig.getKey().equals("test-key")) {
                log.info("Usando simulación de Gemini (API key de prueba)");
                return simularRespuesta();
            }
            
            // Construir request para Gemini
            GeminiRequest.GenerateContentRequest request = new GeminiRequest.GenerateContentRequest(
                List.of(new GeminiRequest.Content(
                    List.of(new GeminiRequest.Part(prompt))
                )),
                new GeminiRequest.GenerationConfig(
                    geminiConfig.getMaxTokens(),
                    geminiConfig.getTemperature()
                )
            );
            
            // Realizar llamada a Gemini
            GeminiResponse.GenerateContentResponse response = geminiWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                    .queryParam("key", geminiConfig.getKey())
                    .build())
                .body(Mono.just(request), GeminiRequest.GenerateContentRequest.class)
                .retrieve()
                .bodyToMono(GeminiResponse.GenerateContentResponse.class)
                .timeout(Duration.ofSeconds(50))
                .block();
            
            if (response == null || response.candidates().isEmpty()) {
                throw new RuntimeException("Respuesta vacía de Gemini");
            }
            
            // Extraer el contenido de la respuesta
            String contenido = response.candidates().get(0).content().parts().get(0).text();
            log.info("Respuesta recibida de Gemini, parseando JSON...");
            
            return parsearRespuestaJson(contenido);
            
        } catch (WebClientResponseException e) {
            log.error("Error de cliente HTTP al llamar Gemini: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al comunicarse con Gemini: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al generar pregunta con Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno al generar pregunta", e);
        }
    }
    
    /**
     * Simula una respuesta de Gemini para pruebas.
     */
    @Override
    public RespuestaGeneracion simularRespuesta() {
        log.debug("Generando respuesta simulada de Gemini");
        
        return new RespuestaGeneracion(
            """
            public class EjemploGemini {
                public static void main(String[] args) {
                    String texto = "Hola Mundo";
                    int longitud = texto.length();
                    System.out.println(longitud);
                }
            }
            """,
            "¿Cuál es la salida de este programa?",
            new String[]{"8", "9", "10", "11"},
            "10",
            "El método length() de String retorna la cantidad de caracteres. 'Hola Mundo' tiene 10 caracteres incluyendo el espacio.",
            "strings",
            "metodos-string",
            "facil"
        );
    }

    /**
     * Parsea la respuesta JSON de Gemini.
     */
    private RespuestaGeneracion parsearRespuestaJson(String contenidoJson) {
        try {
            log.debug("Parseando JSON de Gemini: {}", contenidoJson);
            
            // Limpiar el contenido si viene con markdown
            String jsonLimpio = contenidoJson
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
            
            JsonNode rootNode = objectMapper.readTree(jsonLimpio);
            
            // Extraer campos del JSON
            String codigoJava = rootNode.get("codigoJava").asText();
            String enunciado = rootNode.get("enunciado").asText();
            String respuestaCorrecta = rootNode.get("respuestaCorrecta").asText();
            String explicacion = rootNode.get("explicacion").asText();
            String tematicaPrincipal = rootNode.get("tematicaPrincipal").asText();
            String tematicaSecundaria = rootNode.get("tematicaSecundaria").asText();
            String dificultad = rootNode.get("dificultad").asText();
            
            // Extraer opciones
            JsonNode opcionesNode = rootNode.get("opciones");
            String[] opciones = new String[opcionesNode.size()];
            for (int i = 0; i < opcionesNode.size(); i++) {
                opciones[i] = opcionesNode.get(i).asText();
            }
            
            log.debug("JSON de Gemini parseado exitosamente");
            
            return new RespuestaGeneracion(
                codigoJava, enunciado, opciones, respuestaCorrecta,
                explicacion, tematicaPrincipal, tematicaSecundaria, dificultad
            );
            
        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON de Gemini: {}", e.getMessage());
            log.error("JSON problemático: {}", contenidoJson);
            throw new RuntimeException("Error al parsear respuesta de Gemini", e);
        } catch (Exception e) {
            log.error("Error inesperado al parsear respuesta de Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar respuesta de Gemini", e);
        }
    }

    @Override
    public RespuestaExamen generarExamenCompleto(String prompt) {
        log.info("Enviando prompt a Gemini para generar examen completo");

        try {
            // Si la API key es de prueba, simular respuesta
            if (geminiConfig.getKey().equals("test-key")) {
                log.info("Usando simulación de examen completo (API key de prueba)");
                return simularRespuestaExamen();
            }

            // Construir request para Gemini
            GeminiRequest.GenerateContentRequest request = new GeminiRequest.GenerateContentRequest(
                    List.of(new GeminiRequest.Content(
                            List.of(new GeminiRequest.Part(prompt))
                    )),
                    new GeminiRequest.GenerationConfig(
                            geminiConfig.getMaxTokens(),
                            geminiConfig.getTemperature()
                    )
            );

            // Realizar llamada a Gemini
            GeminiResponse.GenerateContentResponse response = geminiWebClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("key", geminiConfig.getKey())
                            .build())
                    .body(Mono.just(request), GeminiRequest.GenerateContentRequest.class)
                    .retrieve()
                    .bodyToMono(GeminiResponse.GenerateContentResponse.class)
                    .timeout(Duration.ofSeconds(60)) // Aumentar timeout para respuestas grandes
                    .block();

            if (response == null || response.candidates().isEmpty()) {
                throw new RuntimeException("Respuesta vacía de Gemini");
            }

            // Extraer el contenido de la respuesta
            String contenido = response.candidates().get(0).content().parts().get(0).text();
            log.info("Respuesta recibida de Gemini para examen, parseando JSON...");

            return parsearRespuestaExamenJson(contenido);

        } catch (WebClientResponseException e) {
            log.error("Error HTTP al llamar a Gemini: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al comunicarse con Gemini: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al generar examen con Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno al generar examen completo", e);
        }
    }

    @Override
    public RespuestaExamen simularRespuestaExamen() {
        log.debug("Generando examen simulado de Gemini");

        List<RespuestaGeneracion> preguntasSimuladas = List.of(
                new RespuestaGeneracion(
                        """
                        public class Simulada1 {
                            public static void main(String[] args) {
                                int a = 5, b = 10;
                                System.out.println(a + b);
                            }
                        }
                        """,
                        "¿Cuál es la salida del programa anterior?",
                        new String[]{"10", "15", "5", "Error de compilación"},
                        "15",
                        "El programa imprime la suma de a y b: 5 + 10 = 15.",
                        "operadores",
                        "aritméticos",
                        "facil"
                ),
                new RespuestaGeneracion(
                        """
                        public class Simulada2 {
                            public static void main(String[] args) {
                                String s = "Java";
                                System.out.println(s.toUpperCase());
                            }
                        }
                        """,
                        "¿Qué imprime este programa?",
                        new String[]{"JAVA", "java", "Error", "null"},
                        "JAVA",
                        "El método toUpperCase() convierte el texto a mayúsculas.",
                        "strings",
                        "metodos-string",
                        "facil"
                )
                // Puedes añadir más preguntas simuladas aquí
        );

        return new RespuestaExamen(
                "Examen Simulado de Java",
                "Este examen contiene preguntas simuladas para pruebas.",
                preguntasSimuladas
        );
    }

    /**
     * Parsea el JSON de un examen completo devuelto por Gemini.
     */
    private RespuestaExamen parsearRespuestaExamenJson(String contenidoJson) {
        try {
            log.debug("Parseando JSON de examen completo: {}", contenidoJson);

            String jsonLimpio = contenidoJson
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode rootNode = objectMapper.readTree(jsonLimpio);

            String titulo = rootNode.get("titulo").asText();
            String descripcion = rootNode.get("descripcion").asText();

            List<RespuestaGeneracion> preguntas = new ArrayList<>();
            JsonNode preguntasNode = rootNode.get("preguntas");

            for (JsonNode preguntaNode : preguntasNode) {
                String codigoJava = preguntaNode.get("codigoJava").asText();
                String enunciado = preguntaNode.get("enunciado").asText();
                String respuestaCorrecta = preguntaNode.get("respuestaCorrecta").asText();
                String explicacion = preguntaNode.get("explicacion").asText();
                String tematicaPrincipal = preguntaNode.get("tematicaPrincipal").asText();
                String tematicaSecundaria = preguntaNode.get("tematicaSecundaria").asText();
                String dificultad = preguntaNode.get("dificultad").asText();

                // Extraer opciones
                JsonNode opcionesNode = preguntaNode.get("opciones");
                String[] opciones = new String[opcionesNode.size()];
                for (int i = 0; i < opcionesNode.size(); i++) {
                    opciones[i] = opcionesNode.get(i).asText();
                }

                preguntas.add(new RespuestaGeneracion(
                        codigoJava, enunciado, opciones, respuestaCorrecta,
                        explicacion, tematicaPrincipal, tematicaSecundaria, dificultad
                ));
            }

            log.debug("JSON de examen completo parseado exitosamente");

            return new RespuestaExamen(titulo, descripcion, preguntas);

        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON del examen: {}", e.getMessage());
            log.error("JSON problemático: {}", contenidoJson);
            throw new RuntimeException("Error al parsear examen de Gemini", e);
        } catch (Exception e) {
            log.error("Error inesperado al procesar examen de Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar examen completo", e);
        }
    }
}

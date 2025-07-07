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
                return simularRespuestaGemini();
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
                .timeout(Duration.ofSeconds(30))
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
    private RespuestaGeneracion simularRespuestaGemini() {
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
}

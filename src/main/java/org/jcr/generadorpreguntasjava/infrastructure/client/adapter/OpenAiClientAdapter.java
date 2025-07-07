package org.jcr.generadorpreguntasjava.infrastructure.client.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.infrastructure.client.config.OpenAiConfig;
import org.jcr.generadorpreguntasjava.infrastructure.client.dto.OpenAiRequest;
import org.jcr.generadorpreguntasjava.infrastructure.client.dto.OpenAiResponse;
import org.jcr.generadorpreguntasjava.port.out.GeneradorDePreguntaServicePort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Adaptador secundario que implementa el puerto de salida para generar preguntas usando OpenAI.
 * 
 * Se conecta con la API de OpenAI para generar preguntas de código Java.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiClientAdapter implements GeneradorDePreguntaServicePort {
    
    private final WebClient openAiWebClient;
    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;
    
    @Override
    public RespuestaGeneracion generarPregunta(String prompt) {
        log.info("Enviando prompt a OpenAI para generar pregunta");
        
        try {
            // Si la API key es de prueba, simular respuesta
            if (openAiConfig.getKey().equals("sk-test-key")) {
                log.info("Usando simulación de OpenAI (API key de prueba)");
                return simularRespuestaOpenAi();
            }
            
            // Construir request
            OpenAiRequest.ChatCompletionRequest request = new OpenAiRequest.ChatCompletionRequest(
                openAiConfig.getModel(),
                List.of(new OpenAiRequest.Message("user", prompt)),
                openAiConfig.getMaxTokens(),
                openAiConfig.getTemperature()
            );
            
            // Realizar llamada a OpenAI
            OpenAiResponse.ChatCompletionResponse response = openAiWebClient
                .post()
                .body(Mono.just(request), OpenAiRequest.ChatCompletionRequest.class)
                .retrieve()
                .bodyToMono(OpenAiResponse.ChatCompletionResponse.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            if (response == null || response.choices().isEmpty()) {
                throw new RuntimeException("Respuesta vacía de OpenAI");
            }
            
            String contenido = response.choices().get(0).message().content();
            log.info("Respuesta recibida de OpenAI, parseando JSON...");
            
            return parsearRespuestaJson(contenido);
            
        } catch (WebClientResponseException e) {
            log.error("Error de cliente HTTP al llamar OpenAI: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al comunicarse con OpenAI: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al generar pregunta con OpenAI: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno al generar pregunta", e);
        }
    }
    
    /**
     * Simula una respuesta de OpenAI para pruebas.
     */
    private RespuestaGeneracion simularRespuestaOpenAi() {
        log.debug("Generando respuesta simulada");
        
        return new RespuestaGeneracion(
            """
            public class Ejemplo {
                public static void main(String[] args) {
                    int[] numeros = {1, 2, 3, 4, 5};
                    int suma = 0;
                    for (int num : numeros) {
                        suma += num;
                    }
                    System.out.println(suma);
                }
            }
            """,
            "¿Qué valor imprime este código en la consola?",
            new String[]{"10", "15", "20", "25"},
            "15",
            "El código suma todos los elementos del array [1, 2, 3, 4, 5]. La suma es 1+2+3+4+5 = 15.",
            "arrays",
            "bucles-for",
            "facil"
        );
    }
    
    /**
     * Parsea la respuesta JSON de OpenAI.
     */
    private RespuestaGeneracion parsearRespuestaJson(String contenidoJson) {
        try {
            log.debug("Parseando JSON: {}", contenidoJson);
            
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
            
            log.debug("JSON parseado exitosamente");
            
            return new RespuestaGeneracion(
                codigoJava, enunciado, opciones, respuestaCorrecta,
                explicacion, tematicaPrincipal, tematicaSecundaria, dificultad
            );
            
        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON de OpenAI: {}", e.getMessage());
            log.error("JSON problemático: {}", contenidoJson);
            throw new RuntimeException("Error al parsear respuesta de OpenAI", e);
        } catch (Exception e) {
            log.error("Error inesperado al parsear respuesta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar respuesta de OpenAI", e);
        }
    }
}

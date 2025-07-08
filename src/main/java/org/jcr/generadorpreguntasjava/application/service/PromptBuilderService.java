package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Tematica;
import org.jcr.generadorpreguntasjava.domain.service.PromptTemplate;
import org.jcr.generadorpreguntasjava.port.out.TematicaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para construir prompts dinámicos.
 * 
 * Se encarga de obtener las temáticas previamente utilizadas y construir
 * el prompt completo para la generación de preguntas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptBuilderService {

    /**
     * Construye un prompt completo para enviar al generador de preguntas.
     *
     * @param dificultad            Dificultad deseada (puede ser null)
     * @param tematicasDeseadas     Lista de temáticas sobre las cuales generar preguntas
     * @param tematicasYaUtilizadas Lista de temáticas que ya se usaron y deben evitarse
     * @return Prompt completo en formato texto
     */
    public String construirPromptCompleto(String dificultad, List<String> tematicasDeseadas, List<String> tematicasYaUtilizadas) {
        log.info("Construyendo prompt para dificultad: {}, temáticas deseadas: {}, excluyendo: {}",
                dificultad, tematicasDeseadas, tematicasYaUtilizadas);

        // Construye las cadenas de texto para insertar en el prompt
        String tematicasUsadas = construirListaTematicasUsadas(tematicasYaUtilizadas);
        String tematicasDeseadasStr = construirListaTematicasDeseadas(tematicasDeseadas);

        log.debug("Temáticas utilizadas encontradas: {}", tematicasUsadas);

        // Llamada al template que arma el texto final
        String promptCompleto = PromptTemplate.construirPrompt(dificultad, tematicasDeseadasStr, tematicasUsadas);

        log.debug("Prompt construido exitosamente");
        return promptCompleto;
    }

    /**
     * Convierte la lista de temáticas ya utilizadas en un string legible para el prompt.
     *
     * @param tematicas Lista de nombres de temáticas ya usadas
     * @return Cadena con formato "- Temática"
     */
    private String construirListaTematicasUsadas(List<String> tematicas) {
        if (tematicas == null || tematicas.isEmpty()) {
            return "Ninguna temática utilizada aún.";
        }

        return tematicas.stream()
                .map(t -> "- " + t)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Convierte la lista de temáticas deseadas en un string para usar en el prompt.
     *
     * @param tematicas Lista de nombres de temáticas deseadas
     * @return Cadena separada por comas: "bucles, condicionales, arrays"
     */
    private String construirListaTematicasDeseadas(List<String> tematicas) {
        if (tematicas == null || tematicas.isEmpty()) {
            return "cualquier temática";
        }

        return String.join(", ", tematicas);
    }
}
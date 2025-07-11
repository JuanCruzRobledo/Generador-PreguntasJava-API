package org.jcr.generadorpreguntasjava.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.service.PromptTemplate;
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
     * Construye un prompt para generar un examen completo.
     *
     * @param dificultad            Dificultad deseada
     * @param cantidadPreguntas     Número de preguntas requeridas
     * @param tematicasDeseadas     Temáticas preferidas
     * @param tematicasExcluidas    Temáticas a evitar
     * @return Prompt completo para generación de examen
     */
    public String construirPromptExamen(Dificultad dificultad, int cantidadPreguntas,
                                        List<String> tematicasDeseadas, List<String> tematicasExcluidas) {
        log.info("Construyendo prompt para examen con {} preguntas de dificultad {}",
                cantidadPreguntas, dificultad);

        String tematicasStr = construirListaTematicasDeseadas(tematicasDeseadas);
        String excluidasStr = construirListaTematicasUsadas(tematicasExcluidas);

        return PromptTemplate.construirPromptExamen(
                dificultad.name().toLowerCase(),
                cantidadPreguntas,
                tematicasStr,
                excluidasStr
        );
    }

    /**
     * Construye un prompt completo para enviar al generador de preguntas.
     *
     * @param dificultad            Dificultad deseada (puede ser null)
     * @param categoriaPrincipal    Categoria la cual se generan las preguntas (POO,...)
     * @param tagsDeseados     Lista de temáticas sobre las cuales generar preguntas
     * @param tagsYaUtilizadas Lista de temáticas que ya se usaron y deben evitarse
     * @return Prompt completo en formato texto
     */
    public String construirPromptCompleto(String dificultad, String lenguaje, String categoriaPrincipal, List<String> tagsDeseados, List<String> tagsYaUtilizadas) {
        log.info("Construyendo prompt para dificultad: {}, categoria principal: {}, tags deseadas: {}, excluyendo: {}",
                dificultad, categoriaPrincipal,  tagsDeseados, tagsYaUtilizadas);

        // Construye las cadenas de texto para insertar en el prompt
        String tagsUsadosStr = construirListaTematicasUsadas(tagsYaUtilizadas);
        String tagsDeseadosStr = construirListaTematicasDeseadas(tagsDeseados);

        log.debug("Temáticas utilizadas encontradas: {}", tagsYaUtilizadas);

        // Llamada al template que arma el texto final
        String promptCompleto = PromptTemplate.construirPrompt(dificultad, lenguaje, categoriaPrincipal, tagsDeseadosStr, tagsUsadosStr);

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
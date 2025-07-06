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
    
    private final TematicaRepositoryPort tematicaRepositoryPort;
    
    /**
     * Construye un prompt completo incluyendo las temáticas ya utilizadas.
     * 
     * @param dificultad Dificultad deseada (puede ser null)
     * @param tematicaDeseada Temática específica deseada (puede ser null)
     * @return Prompt completo para la generación
     */
    public String construirPromptCompleto(String dificultad, String tematicaDeseada) {
        log.info("Construyendo prompt para dificultad: {} y temática: {}", dificultad, tematicaDeseada);
        
        // Obtener todas las temáticas utilizadas
        List<Tematica> tematicasUtilizadas = tematicaRepositoryPort.obtenerTodas();
        
        // Construir la lista de temáticas ya usadas
        String tematicasUsadas = construirListaTematicasUsadas(tematicasUtilizadas);
        
        log.debug("Temáticas utilizadas encontradas: {}", tematicasUsadas);
        
        // Construir el prompt completo
        String promptCompleto = PromptTemplate.construirPrompt(dificultad, tematicaDeseada, tematicasUsadas);
        
        log.debug("Prompt construido exitosamente");
        return promptCompleto;
    }
    
    /**
     * Construye una cadena de texto con las temáticas ya utilizadas.
     */
    private String construirListaTematicasUsadas(List<Tematica> tematicas) {
        if (tematicas.isEmpty()) {
            return "Ninguna temática utilizada aún";
        }
        
        return tematicas.stream()
            .map(tematica -> String.format("- %s (usada %d veces)", 
                                         tematica.nombre(), 
                                         tematica.contadorUsos()))
            .collect(Collectors.joining("\\n"));
    }
}

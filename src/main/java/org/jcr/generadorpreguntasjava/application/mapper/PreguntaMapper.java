package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.ValidarRespuestaPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.*;
import org.jcr.generadorpreguntasjava.port.in.web.dto.request.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper para convertir entre entidades del dominio y DTOs.
 */
@Mapper(componentModel = "spring")
public interface PreguntaMapper {
    
    // === MAPPING DE PREGUNTA ===
    
    @Mapping(target = "dificultad", source = "dificultad", qualifiedByName = "dificultadToString")
    PreguntaResponse toResponse(Pregunta pregunta);
    
    List<PreguntaResponse> toResponseList(List<Pregunta> preguntas);
    
    // === MAPPING DE OPCION ===
    
    OpcionResponse toResponse(Opcion opcion);
    
    List<OpcionResponse> toOpcionResponseList(List<Opcion> opciones);
    
    // === MAPPING DE TEMATICA ===
    
    TematicaResponse toResponse(Tematica tematica);
    
    List<TematicaResponse> toTematicaResponseList(List<Tematica> tematicas);
    
    // === MAPPING DE VALIDACION ===
    
    @Mapping(target = "esCorrecta", source = "esCorrecta")
    @Mapping(target = "explicacion", source = "explicacion")
    @Mapping(target = "respuestaCorrecta", source = "respuestaCorrecta")
    ValidacionResponse toResponse(ValidarRespuestaPort.ResultadoValidacion resultado);
    
    // === MAPPING DE REQUEST A DOMINIO ===
    
    @Mapping(target = ".", source = "dificultad", qualifiedByName = "stringToDificultad")
    default Dificultad mapDificultad(GenerarPreguntaRequest request) {
        return stringToDificultad(request.dificultad());
    }
    
    // === MÉTODOS AUXILIARES ===
    
    @Named("dificultadToString")
    default String dificultadToString(Dificultad dificultad) {
        return dificultad != null ? dificultad.name().toLowerCase() : null;
    }
    
    @Named("stringToDificultad")
    default Dificultad stringToDificultad(String dificultad) {
        if (dificultad == null || dificultad.trim().isEmpty()) {
            return null;
        }
        try {
            return Dificultad.fromString(dificultad);
        } catch (IllegalArgumentException e) {
            // En caso de dificultad inválida, retornar null
            return null;
        }
    }
}

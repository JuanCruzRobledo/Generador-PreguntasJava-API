package org.jcr.generadorpreguntasjava.application.mapper;

import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.port.in.ValidarRespuestaPort;
import org.jcr.generadorpreguntasjava.port.in.ConsultarEstadisticasPort;
import org.jcr.generadorpreguntasjava.port.in.web.dto.response.*;
import org.jcr.generadorpreguntasjava.port.in.web.dto.request.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Mapper para convertir entre entidades del dominio y DTOs.
 */
@Mapper(componentModel = "spring", uses = {OpcionMapper.class, TematicaMapper.class})
public interface PreguntaMapper {
    
    // === MAPPING DE PREGUNTA ===
    
    @Mapping(target = "dificultad", source = "dificultad", qualifiedByName = "dificultadToString")
    PreguntaResponse toResponse(Pregunta pregunta);
    
    List<PreguntaResponse> toResponseList(List<Pregunta> preguntas);

    
    // === MAPPING DE VALIDACION ===
    
    @Mapping(target = "esCorrecta", source = "esCorrecta")
    @Mapping(target = "explicacion", source = "explicacion")
    @Mapping(target = "respuestaCorrecta", source = "respuestaCorrecta")
    ValidacionResponse toResponse(ValidarRespuestaPort.ResultadoValidacion resultado);
    
    // === MAPPING DE USUARIO ===
    
    @Mapping(target = "nombreParaMostrar", source = ".", qualifiedByName = "calcularNombreParaMostrar")
    UsuarioResponse toUsuarioResponse(Usuario usuario);
    
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
    
    @Named("calcularNombreParaMostrar")
    default String calcularNombreParaMostrar(Usuario usuario) {
        return usuario.getNombreParaMostrar();
    }
    
    // === MAPPING DE ESTADÍSTICAS ===
    
    default EstadisticasResponse toResponse(EstadisticasUsuario estadisticas, ConsultarEstadisticasPort.ResumenProgreso resumen) {
        Map<String, EstadisticasDificultadResponse> porDificultad = new HashMap<>();
        estadisticas.porDificultad().forEach((dif, stats) -> 
            porDificultad.put(dif.name().toLowerCase(), toResponse(stats))
        );
        
        Map<String, EstadisticasTematicaResponse> porTematica = new HashMap<>();
        estadisticas.porTematica().forEach((tema, stats) -> 
            porTematica.put(tema, toResponse(stats))
        );
        
        return new EstadisticasResponse(
            estadisticas.totalPreguntas(),
            estadisticas.respuestasCorrectas(),
            estadisticas.porcentajeAciertos(),
            estadisticas.getTiempoPromedioFormateado(),
            porDificultad,
            porTematica,
            resumen.nivelUsuario(),
            estadisticas.tieneBuenRendimiento()
        );
    }
    
    default EstadisticasDificultadResponse toResponse(EstadisticasPorDificultad estadisticas) {
        return new EstadisticasDificultadResponse(
            estadisticas.dificultad().name().toLowerCase(),
            estadisticas.totalPreguntas(),
            estadisticas.respuestasCorrectas(),
            estadisticas.porcentajeAciertos(),
            estadisticas.getTiempoPromedioFormateado(),
            estadisticas.esBuenRendimiento()
        );
    }
    
    default EstadisticasTematicaResponse toResponse(EstadisticasPorTematica estadisticas) {
        return new EstadisticasTematicaResponse(
            estadisticas.tematica(),
            estadisticas.totalPreguntas(),
            estadisticas.respuestasCorrectas(),
            estadisticas.porcentajeAciertos(),
            estadisticas.getTiempoPromedioFormateado(),
            estadisticas.esBuenRendimiento(),
            estadisticas.esTematicaFavorita()
        );
    }
}

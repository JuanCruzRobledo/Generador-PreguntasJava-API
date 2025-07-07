package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * Mapper para convertir entre entidades del dominio y entidades JPA.
 */
@Mapper(componentModel = "spring")
public interface PersistenceMapper {
    
    // === MAPPING DE PREGUNTA ===
    
    @Mapping(target = "opciones", source = "opciones", qualifiedByName = "mapOpcionesFromEntity")
    @Mapping(target = "tematicas", source = "tematicas", qualifiedByName = "mapTematicasFromEntity")
    Pregunta toDomain(PreguntaEntity entity);
    
    @Mapping(target = "opciones", source = "opciones", qualifiedByName = "mapOpcionesToEntity")
    @Mapping(target = "tematicas", source = "tematicas", qualifiedByName = "mapTematicasToEntity")
    PreguntaEntity toEntity(Pregunta domain);
    
    List<Pregunta> toDomainList(List<PreguntaEntity> entities);
    
    // === MAPPING DE OPCION ===

    Opcion toDomain(OpcionEntity entity);
    
    @Mapping(target = "pregunta", ignore = true)
    OpcionEntity toEntity(Opcion domain);
    
    // === MAPPING DE TEMATICA ===


    Tematica toDomain(TematicaEntity entity);
    
    @Mapping(target = "preguntas", ignore = true)
    TematicaEntity toEntity(Tematica domain);
    
    List<Tematica> toDomainTematicaList(List<TematicaEntity> entities);
    
    // === MÉTODOS AUXILIARES ===
    
    @Named("mapOpcionesFromEntity")
    default List<Opcion> mapOpcionesFromEntity(List<OpcionEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
            .map(this::toDomain)
            .toList();
    }
    
    @Named("mapOpcionesToEntity")
    default List<OpcionEntity> mapOpcionesToEntity(List<Opcion> domain) {
        if (domain == null) return List.of();
        return domain.stream()
            .map(this::toEntity)
            .toList();
    }
    
    @Named("mapTematicasFromEntity")
    default List<Tematica> mapTematicasFromEntity(Set<TematicaEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
            .map(this::toDomain)
            .toList();
    }
    
    @Named("mapTematicasToEntity")
    default Set<TematicaEntity> mapTematicasToEntity(List<Tematica> domain) {
        if (domain == null) return Set.of();
        return domain.stream()
            .map(this::toEntity)
            .collect(java.util.stream.Collectors.toSet());
    }
    
    // === MAPPING DE USUARIO ===
    
    Usuario toDomain(UsuarioEntity entity);
    
    UsuarioEntity toEntity(Usuario domain);
    
    List<Usuario> toDomainUsuarioList(List<UsuarioEntity> entities);
    
    // === MAPPING DE SESION RESPUESTA ===
    
    @Mapping(target = "tiempoRespuesta", source = ".", qualifiedByName = "mapTiempoRespuestaFromEntity")
    SesionRespuesta toDomain(SesionRespuestaEntity entity);
    
    @Mapping(target = "tiempoRespuestaMs", source = ".", qualifiedByName = "mapTiempoRespuestaToEntity")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pregunta", ignore = true)
    SesionRespuestaEntity toEntity(SesionRespuesta domain);
    
    List<SesionRespuesta> toDomainSesionList(List<SesionRespuestaEntity> entities);
    
    // === MAPPING DE ESTADISTICAS USUARIO ===
    
    @Mapping(target = "tiempoPromedio", source = ".", qualifiedByName = "mapTiempoPromedioFromEntity")
    @Mapping(target = "porDificultad", source = ".", qualifiedByName = "mapEstadisticasDificultadFromEntity")
    @Mapping(target = "porTematica", source = ".", qualifiedByName = "mapEstadisticasTematicaFromEntity")
    EstadisticasUsuario toDomain(EstadisticasUsuarioEntity entity);
    
    @Mapping(target = "tiempoPromedioMs", source = ".", qualifiedByName = "mapTiempoPromedioToEntity")
    @Mapping(target = "estadisticasPorDificultadJson", source = ".", qualifiedByName = "mapEstadisticasDificultadToEntity")
    @Mapping(target = "estadisticasPorTematicaJson", source = ".", qualifiedByName = "mapEstadisticasTematicaToEntity")
    @Mapping(target = "usuario", ignore = true)
    EstadisticasUsuarioEntity toEntity(EstadisticasUsuario domain);
    
    List<EstadisticasUsuario> toDomainEstadisticasList(List<EstadisticasUsuarioEntity> entities);
    
    // === MÉTODOS AUXILIARES PARA TIEMPO Y ESTADÍSTICAS ===
    
    @Named("mapTiempoRespuestaFromEntity")
    default Duration mapTiempoRespuestaFromEntity(SesionRespuestaEntity entity) {
        return entity.getTiempoRespuestaDuration();
    }
    
    @Named("mapTiempoRespuestaToEntity")
    default Long mapTiempoRespuestaToEntity(SesionRespuesta domain) {
        return domain.tiempoRespuesta() != null ? domain.tiempoRespuesta().toMillis() : null;
    }
    
    @Named("mapTiempoPromedioFromEntity")
    default Duration mapTiempoPromedioFromEntity(EstadisticasUsuarioEntity entity) {
        return entity.getTiempoPromedioMs() != null ? Duration.ofMillis(entity.getTiempoPromedioMs()) : Duration.ZERO;
    }
    
    @Named("mapTiempoPromedioToEntity")
    default Long mapTiempoPromedioToEntity(EstadisticasUsuario domain) {
        return domain.tiempoPromedio() != null ? domain.tiempoPromedio().toMillis() : null;
    }
    
    @Named("mapEstadisticasDificultadFromEntity")
    default Map<Dificultad, EstadisticasPorDificultad> mapEstadisticasDificultadFromEntity(EstadisticasUsuarioEntity entity) {
        if (entity.getEstadisticasPorDificultadJson() == null) {
            return new HashMap<>();
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Map<String, Object>>> typeRef = new TypeReference<>() {};
            Map<String, Map<String, Object>> jsonMap = mapper.readValue(entity.getEstadisticasPorDificultadJson(), typeRef);
            
            Map<Dificultad, EstadisticasPorDificultad> result = new HashMap<>();
            
            for (Map.Entry<String, Map<String, Object>> entry : jsonMap.entrySet()) {
                try {
                    Dificultad dificultad = Dificultad.valueOf(entry.getKey());
                    Map<String, Object> data = entry.getValue();
                    
                    EstadisticasPorDificultad stats = new EstadisticasPorDificultad(
                        dificultad,
                        ((Number) data.get("totalPreguntas")).intValue(),
                        ((Number) data.get("respuestasCorrectas")).intValue(),
                        ((Number) data.get("porcentajeAciertos")).doubleValue(),
                        Duration.ofMillis(((Number) data.get("tiempoPromedioMs")).longValue())
                    );
                    
                    result.put(dificultad, stats);
                } catch (Exception e) {
                    // Skip invalid entries
                }
            }
            
            return result;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    @Named("mapEstadisticasDificultadToEntity")
    default String mapEstadisticasDificultadToEntity(EstadisticasUsuario domain) {
        if (domain.porDificultad() == null || domain.porDificultad().isEmpty()) {
            return null;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Object>> jsonMap = new HashMap<>();
            
            for (Map.Entry<Dificultad, EstadisticasPorDificultad> entry : domain.porDificultad().entrySet()) {
                EstadisticasPorDificultad stats = entry.getValue();
                Map<String, Object> data = new HashMap<>();
                data.put("totalPreguntas", stats.totalPreguntas());
                data.put("respuestasCorrectas", stats.respuestasCorrectas());
                data.put("porcentajeAciertos", stats.porcentajeAciertos());
                data.put("tiempoPromedioMs", stats.tiempoPromedio().toMillis());
                
                jsonMap.put(entry.getKey().name(), data);
            }
            
            return mapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Named("mapEstadisticasTematicaFromEntity")
    default Map<String, EstadisticasPorTematica> mapEstadisticasTematicaFromEntity(EstadisticasUsuarioEntity entity) {
        if (entity.getEstadisticasPorTematicaJson() == null) {
            return new HashMap<>();
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Map<String, Object>>> typeRef = new TypeReference<>() {};
            Map<String, Map<String, Object>> jsonMap = mapper.readValue(entity.getEstadisticasPorTematicaJson(), typeRef);
            
            Map<String, EstadisticasPorTematica> result = new HashMap<>();
            
            for (Map.Entry<String, Map<String, Object>> entry : jsonMap.entrySet()) {
                try {
                    String tematica = entry.getKey();
                    Map<String, Object> data = entry.getValue();
                    
                    EstadisticasPorTematica stats = new EstadisticasPorTematica(
                        tematica,
                        ((Number) data.get("totalPreguntas")).intValue(),
                        ((Number) data.get("respuestasCorrectas")).intValue(),
                        ((Number) data.get("porcentajeAciertos")).doubleValue(),
                        Duration.ofMillis(((Number) data.get("tiempoPromedioMs")).longValue())
                    );
                    
                    result.put(tematica, stats);
                } catch (Exception e) {
                    // Skip invalid entries
                }
            }
            
            return result;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    @Named("mapEstadisticasTematicaToEntity")
    default String mapEstadisticasTematicaToEntity(EstadisticasUsuario domain) {
        if (domain.porTematica() == null || domain.porTematica().isEmpty()) {
            return null;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Object>> jsonMap = new HashMap<>();
            
            for (Map.Entry<String, EstadisticasPorTematica> entry : domain.porTematica().entrySet()) {
                EstadisticasPorTematica stats = entry.getValue();
                Map<String, Object> data = new HashMap<>();
                data.put("totalPreguntas", stats.totalPreguntas());
                data.put("respuestasCorrectas", stats.respuestasCorrectas());
                data.put("porcentajeAciertos", stats.porcentajeAciertos());
                data.put("tiempoPromedioMs", stats.tiempoPromedio().toMillis());
                
                jsonMap.put(entry.getKey(), data);
            }
            
            return mapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

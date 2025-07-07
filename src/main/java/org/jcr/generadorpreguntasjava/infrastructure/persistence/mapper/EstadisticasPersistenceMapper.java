package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcr.generadorpreguntasjava.domain.model.Dificultad;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasPorDificultad;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasPorTematica;
import org.jcr.generadorpreguntasjava.domain.model.EstadisticasUsuario;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.EstadisticasUsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface EstadisticasPersistenceMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "tiempoPromedio", source = "tiempoPromedioMs", qualifiedByName = "msToDuration")
    @Mapping(target = "porDificultad", source = "estadisticasPorDificultadJson", qualifiedByName = "jsonToDificultadMap")
    @Mapping(target = "porTematica", source = "estadisticasPorTematicaJson", qualifiedByName = "jsonToTematicaMap")
    EstadisticasUsuario toDomain(EstadisticasUsuarioEntity entity);

    @Mapping(target = "tiempoPromedioMs", source = "tiempoPromedio", qualifiedByName = "durationToMs")
    @Mapping(target = "estadisticasPorDificultadJson", source = "porDificultad", qualifiedByName = "dificultadMapToJson")
    @Mapping(target = "estadisticasPorTematicaJson", source = "porTematica", qualifiedByName = "tematicaMapToJson")
    EstadisticasUsuarioEntity toEntity(EstadisticasUsuario domain);

    List<EstadisticasUsuario> toDomainList(List<EstadisticasUsuarioEntity> entities);

    List<EstadisticasUsuarioEntity> toEntityList(List<EstadisticasUsuario> domains);

    @Named("msToDuration")
    default Duration msToDuration(Long millis) {
        return millis != null ? Duration.ofMillis(millis) : Duration.ZERO;
    }

    @Named("durationToMs")
    default Long durationToMs(Duration duration) {
        return duration != null ? duration.toMillis() : 0L;
    }

    @Named("jsonToDificultadMap")
    default Map<Dificultad, EstadisticasPorDificultad> jsonToDificultadMap(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return OBJECT_MAPPER.readValue(json,
                    new TypeReference<Map<Dificultad, EstadisticasPorDificultad>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir JSON a Map<Dificultad, EstadisticasPorDificultad>", e);
        }
    }

    @Named("dificultadMapToJson")
    default String dificultadMapToJson(Map<Dificultad, EstadisticasPorDificultad> map) {
        try {
            return map != null ? OBJECT_MAPPER.writeValueAsString(map) : "{}";
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir Map<Dificultad, EstadisticasPorDificultad> a JSON", e);
        }
    }

    @Named("jsonToTematicaMap")
    default Map<String, EstadisticasPorTematica> jsonToTematicaMap(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return OBJECT_MAPPER.readValue(json,
                    new TypeReference<Map<String, EstadisticasPorTematica>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir JSON a Map<String, EstadisticasPorTematica>", e);
        }
    }

    @Named("tematicaMapToJson")
    default String tematicaMapToJson(Map<String, EstadisticasPorTematica> map) {
        try {
            return map != null ? OBJECT_MAPPER.writeValueAsString(map) : "{}";
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir Map<String, EstadisticasPorTematica> a JSON", e);
        }
    }
}
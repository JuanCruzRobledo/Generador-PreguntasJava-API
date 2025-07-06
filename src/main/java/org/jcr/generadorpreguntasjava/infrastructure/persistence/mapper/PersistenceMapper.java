package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.*;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

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
}

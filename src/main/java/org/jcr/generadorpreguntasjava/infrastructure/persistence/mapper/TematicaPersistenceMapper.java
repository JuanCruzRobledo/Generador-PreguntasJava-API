package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.TagTematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TagTematicaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TematicaPersistenceMapper {
    // === MAPPING DE TEMATICA ===


    TagTematica toDomain(TagTematicaEntity entity);

    @Mapping(target = "preguntas", ignore = true)
    TagTematicaEntity toEntity(TagTematica domain);

    List<TagTematica> toDomainTematicaList(List<TagTematicaEntity> entities);
}

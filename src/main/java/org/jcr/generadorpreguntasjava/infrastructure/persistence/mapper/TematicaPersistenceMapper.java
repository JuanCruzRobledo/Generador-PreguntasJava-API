package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Tematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.TematicaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TematicaPersistenceMapper {
    // === MAPPING DE TEMATICA ===


    Tematica toDomain(TematicaEntity entity);

    @Mapping(target = "preguntas", ignore = true)
    TematicaEntity toEntity(Tematica domain);

    List<Tematica> toDomainTematicaList(List<TematicaEntity> entities);
}

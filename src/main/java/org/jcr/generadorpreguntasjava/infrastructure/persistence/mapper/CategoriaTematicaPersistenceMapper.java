package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.CategoriaTematica;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.CategoriaTematicaEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaTematicaPersistenceMapper {

    CategoriaTematicaEntity toEntity(CategoriaTematica categoriaTematica);
    List<CategoriaTematicaEntity> toEntityList(List<CategoriaTematica> categoriaTematica);

    CategoriaTematica toDomain(CategoriaTematicaEntity entity);
    List<CategoriaTematica> toDomainList(List<CategoriaTematicaEntity> categoriaTematica);

}

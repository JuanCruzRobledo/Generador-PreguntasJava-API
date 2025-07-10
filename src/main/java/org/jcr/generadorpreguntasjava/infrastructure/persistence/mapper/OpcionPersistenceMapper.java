package org.jcr.generadorpreguntasjava.infrastructure.persistence.mapper;

import org.jcr.generadorpreguntasjava.domain.model.Opcion;
import org.jcr.generadorpreguntasjava.infrastructure.persistence.entity.OpcionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpcionPersistenceMapper {
    // === MAPPING DE OPCION ===

    Opcion toDomain(OpcionEntity entity);

    @Mapping(target = "pregunta", ignore = true)
    OpcionEntity toEntity(Opcion domain);
}
